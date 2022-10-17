package controller.developerController;

import model.config.DatabaseManagerConnector;
import model.config.Migration;
import model.config.PropertiesConfig;
import model.dto.DeveloperDto;
import model.service.*;
import model.storage.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet(urlPatterns = "/developer/developer_info")
public class DeveloperInfo extends HttpServlet {
    private static DatabaseManagerConnector managerConnector;
    private static DeveloperStorage developerStorage;
    private static DeveloperService developerService;
    private static CompanyStorage companyStorage;
    private static CompanyService companyService;
    private static CustomerStorage customerStorage;
    private static CustomerService customerService;
    private static ProjectStorage projectStorage;
    private static ProjectService projectService;
    private static SkillService skillService;
    private static SkillStorage skillStorage;
    private static RelationStorage relationStorage;
    private static RelationService relationService;

    @Override
    public void init() throws ServletException {
        String dbPassword = System.getenv("dbPassword");
        String dbUsername = System.getenv("dbusername");
        PropertiesConfig propertiesConfig = new PropertiesConfig();
        Properties properties = propertiesConfig.loadProperties("application.properties");
        managerConnector = new DatabaseManagerConnector(properties, dbUsername, dbPassword);
        new Migration(managerConnector).initDb();
        try {
            skillStorage = new SkillStorage(managerConnector);
            skillService = new SkillService(skillStorage);
            relationStorage = new RelationStorage(managerConnector);
            relationService = new RelationService(relationStorage);
            companyStorage = new CompanyStorage(managerConnector);
            companyService = new CompanyService(companyStorage);
            customerStorage = new CustomerStorage(managerConnector);
            customerService = new CustomerService(customerStorage);
            developerStorage = new DeveloperStorage(managerConnector, companyStorage, skillStorage);
            projectStorage = new ProjectStorage(managerConnector, companyStorage, customerStorage);
            projectService = new ProjectService(projectStorage, developerStorage, companyService,
                    customerService, relationService);
            developerService = new DeveloperService(developerStorage, projectService, projectStorage,
                    skillStorage, companyStorage, relationService, skillService);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lastName = req.getParameter("lastName");
        String firstName = req.getParameter("firstName");
        List<String> projects = new ArrayList<>();
        List<String> skills = new ArrayList<>();
        boolean isPresent = false;
        DeveloperDto developerDto = developerService.getByName(lastName, firstName);
        if (developerDto != null) {
            isPresent = true;
            projects = projectService.getProjectsNameByDeveloperId(developerDto.getDeveloper_id());
            skills =  skillService.getSkillSetByDeveloperId(developerDto.getDeveloper_id());
        }
        req.setAttribute("isPresent", isPresent);
        req.setAttribute("developer", developerDto);
        req.setAttribute("projects", projects);
        req.setAttribute("skills", skills);
        req.getRequestDispatcher("/WEB-INF/view/developer/developerInfo.jsp").forward(req, resp);
    }

}
