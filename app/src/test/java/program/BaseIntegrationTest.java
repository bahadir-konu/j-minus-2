package program;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: Bahadir Konu (bah.konu@gmail.com)
 * Date: 2011-05-02
 * Time: 3:06:18 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
public class BaseIntegrationTest {

    @Autowired
    protected ApplicationContext applicationContext;

    @Test
    public void dummyTest() {

    }

    protected String getSourceCodeFrom(String file) throws IOException {
        Resource resource = applicationContext.getResource(file);

        return getContents(resource.getFile());
    }

    private String getContents(File aFile) {
        StringBuilder contents = new StringBuilder();

        try {
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }


}
