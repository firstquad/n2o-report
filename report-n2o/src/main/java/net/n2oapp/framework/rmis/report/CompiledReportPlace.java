package net.n2oapp.framework.rmis.report;

import net.n2oapp.framework.api.metadata.local.Compilable;
import net.n2oapp.framework.api.metadata.local.CompiledMetadata;
import net.n2oapp.framework.api.metadata.local.N2oCompiler;
import net.n2oapp.framework.api.metadata.local.context.CompileContext;
import ru.kirkazan.rmis.app.report.n2o.place.model.N2oReportPlace;


/**
 * Created by dfirstov on 28.10.2014.
 */
public class CompiledReportPlace extends CompiledMetadata<N2oReportPlace, CompileContext> {

    private N2oReportPlace source;

    @Override
    public void compile(N2oReportPlace source, N2oCompiler compiler, CompileContext context) {
        super.compile(source, compiler, context);
        this.source = compiler.copy(source);
    }

    @Override
    public Class<N2oReportPlace> getSourceClass() {
        return N2oReportPlace.class;
    }

    @Override
    public Class<? extends Compilable> getBaseClass() {
        return CompiledReportPlace.class;
    }

    public N2oReportPlace getSource() {
        return source;
    }
}
