package net.n2oapp.framework.report.integration;

import java.util.Properties;

/**
 * @author dfirstov
 * @since 24.06.2015
 */
public class TestN2oProperties extends Properties {

    public void setBirtHost(String value) {
        super.setProperty("rmis.report.url", value);
    }

    public void setFormLocation(String value) {
        super.setProperty("rmis.report.form.path", value);
    }
}
