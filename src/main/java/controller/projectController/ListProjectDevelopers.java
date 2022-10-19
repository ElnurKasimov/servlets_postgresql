package controller.projectController;

import model.config.DatabaseManagerConnector;
import model.config.Migration;
import model.config.PropertiesConfig;
import model.service.*;
import model.storage.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet(urlPatterns = "/project/list_project_developers")
public class ListProjectDevelopers extends HttpServlet {
    private static DatabaseManagerConnector managerConnector;
    private static DeveloperStorage developerStorage;
    private static CompanyStorage companyStorage;
    private static CompanyService companyService;
    private static CustomerStorage customerStorage;
    private static CustomerService customerService;
    private static ProjectStorage projectStorage;
    private static ProjectService projectService;
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String projectName = req.getParameter("projectName");
        String result = "";
        List<String> projects = new ArrayList<>();
        if(projectService.isExist(projectName)) {
            result = "Such developers develop the project '" + projectName + "' :";
            projects = projectService.getDevelopersNamesByProjectName(projectName);
        } else {
            result = "There is no project with such  mane in the database. Please enter correct name.";
        }
        req.setAttribute("projects", projects);
        req.setAttribute("result", result);
        req.getRequestDispatcher("/WEB-INF/view/project/listProjectDevelopers.jsp").forward(req, resp);

    }




}
