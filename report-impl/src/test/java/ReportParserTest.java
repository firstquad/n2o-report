import org.junit.Test;
import ru.kirkazan.rmis.app.report.n2o.impl.parser.ReportParam;
import ru.kirkazan.rmis.app.report.n2o.impl.parser.ReportParser;
import java.io.File;
import java.util.List;

/**
 * Created by dfirstov on 19.09.2014.
 */
public class ReportParserTest {
    @Test
    public void testParse() throws Exception {
        File file1 = new File(this.getClass().getClassLoader().getResource("test1.xml").getFile());
        List<ReportParam> reportParser1 = ReportParser.getInstance().parseParams(file1);
        assert reportParser1.size() == 5 : "In test1 wrong size of list with parameters!";
        assert reportParser1.get(0).getId().equals("14756") : "In test1 id of first parameter is wrong!";
        assert reportParser1.get(0).getRequired() : "In test1 required of first parameter is wrong!";
        assert reportParser1.get(0).getType().equals("string") : "In test1 type of first parameter is wrong!";
        assert reportParser1.get(4).getId().equals("14760") : "In test1 id of last parameter is wrong!";
        assert !reportParser1.get(4).getRequired() : "In test1 required of last parameter is wrong!";
        assert reportParser1.get(4).getType().equals("integer") : "In test1 type of last parameter is wrong!";
        File file2 = new File(this.getClass().getClassLoader().getResource("test2.xml").getFile());
        List<ReportParam> reportParser2 = ReportParser.getInstance().parseParams(file2);
        assert reportParser2.size() == 1 : "In test2 wrong size of list with parameters!";
        assert reportParser2.get(0).getId().equals("14756") : "In test2 id of first parameter is wrong!";
        assert reportParser2.get(0).getRequired() : "In test2 required of first parameter is wrong!";
        assert reportParser2.get(0).getType().equals("string") : "In test2 type of last parameter is wrong!";
    }
}
