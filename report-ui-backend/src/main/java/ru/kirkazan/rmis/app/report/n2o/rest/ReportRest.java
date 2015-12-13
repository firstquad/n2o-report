package ru.kirkazan.rmis.app.report.n2o.rest;

import org.springframework.beans.factory.annotation.Autowired;
import ru.kirkazan.common.jax.response.PostResponse;
import ru.kirkazan.rmis.app.report.n2o.backend.model.ReportForm;
import ru.kirkazan.rmis.app.report.n2o.backend.service.ReportFormController;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by dfirstov on 14.01.2015.
 */
@Path("/report")
@Produces("application/json;charset=UTF-8")
public class ReportRest {

    @Autowired
    private ReportFormController reportFormController;

    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public PostResponse deleteReport(ReportForm reportForm)  throws ParseException, IOException {
        return new PostResponse(reportFormController.deleteReport(reportForm));
    }

    public void setReportFormController(ReportFormController reportFormController) {
        this.reportFormController = reportFormController;
    }
}
