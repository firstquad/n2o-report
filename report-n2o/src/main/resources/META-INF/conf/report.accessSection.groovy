import net.n2oapp.context.StaticSpringContext
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm
import net.n2oapp.framework.config.register.ConfigRegister
import ru.kirkazan.rmis.app.report.n2o.api.model.Report
import ru.kirkazan.rmis.app.report.n2o.api.service.ReportDAO
import ru.kirkazan.rmis.app.report.n2o.form.ReportFormService
import ru.kirkazan.rmis.app.report.n2o.sec.ReportAccessPoint
import ru.kirkazan.rmis.app.security.schema.model.source.*

reportsSection = new AccessSection()
reportsSection.id = 'reports'
reportsSection.name = 'Отчеты'
reportsSection.permissionGroups = [new AccessScheme.PermissionGroup(id: 'read')]
reportsSection.defaultAccess = DefaultAccess.allowed

def reportGroups = new ArrayList<AccessElements>()
reportGroups.add(new AccessElements(id: 'all', name: '/', elements: new ArrayList<AccessElement>()))
ReportDAO reportDAO = (ReportDAO) StaticSpringContext.getBean("reportDAO")
def reports = reportDAO.retrieveReports()
reports.unique { a, b -> a.fileName <=> b.fileName }
reports.eachWithIndex { it, i ->
    def reportElement = new AccessElement()
    def fileName = it.fileName
    reportElement.id = fileName
    processReportName(it)
    reportElement.name = it.name + ' (' + fileName + ')'
    def permission = new AccessElement.Permission(id: 'read', name: "Отчет '${it.name}'", groupId: 'read')
    permission.accessPoints = [new ReportAccessPoint(code: fileName,)]
    reportElement.permissions = [permission]
    def groupName = "all"
    if (fileName.contains("/"))
        groupName = fileName.substring(0, fileName.indexOf("/"))
    AccessElements group = reportGroups.find { g -> g.id.equals(groupName) }
    def maxSize = 100
    if (group != null && group.elements.size() < maxSize)
        group.elements.add(reportElement)
    else {
        def elements = new ArrayList<AccessElement>()
        elements.add(reportElement)
        if (group != null) {
            processOverMaxSize(group, groupName)
        } else {
            group = new AccessElements(id: groupName, name: groupName, elements: elements)
        }
        reportGroups.add(group)
    }
}

private void processOverMaxSize(AccessElements group, String groupName) {
    def lastChar = group.name?.substring(group?.name?.length() - 1)
    if (lastChar != null && Character.isDigit(lastChar as char)) {
        lastChar = (lastChar as Integer)++;
        group.name = groupName + lastChar
    } else
        group.name += '1'
}

private void processReportName(Report it) {
    if (it.name == null)
        it.name = ""
    if (it.formId != null && ConfigRegister.instance.contains(it.formId, N2oForm.class)) {
        def form = ReportFormService.retrieveForm(it.formId)
        if (form?.name != null) {
            it.name = form.name
        }
    }
}

reportsSection.elements = reportGroups