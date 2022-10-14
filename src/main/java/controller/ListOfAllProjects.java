package controller;

import model.config.DatabaseManagerConnector;
import model.config.Migration;
import model.config.PropertiesConfig;
import model.dao.ProjectDao;
import model.dto.DeveloperDto;
import model.dto.ProjectDto;
import model.service.*;
import model.service.converter.DeveloperConverter;
import model.service.converter.ProjectConverter;
import model.storage.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@WebServlet(urlPatterns = "/project/list_all_projects")
public class ListOfAllProjects extends HttpServlet {
    private static DatabaseManagerConnector managerConnector;
    private static CompanyStorage companyStorage;
    private static CustomerStorage customerStorage;
    private static ProjectStorage projectStorage;

    @Override
    public void init() throws ServletException {
        String dbPassword = System.getenv("dbPassword");
        String dbUsername = System.getenv("dbusername");
        PropertiesConfig propertiesConfig = new PropertiesConfig();
        Properties properties = propertiesConfig.loadProperties("application.properties");
        managerConnector = new DatabaseManagerConnector(properties, dbUsername, dbPassword);
        new Migration(managerConnector).initDb();
        try {
            companyStorage = new CompanyStorage(managerConnector);
            customerStorage = new CustomerStorage(managerConnector);
            projectStorage = new ProjectStorage(managerConnector, companyStorage, customerStorage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ProjectDto> projects = projectStorage.findAll().stream()
                .map(Optional::get)
                .map(ProjectDao::getProject_name)
                .map(name -> projectStorage.findByName(name))
                .map(project -> project.get())
                .map(ProjectConverter::from)
                .toList();

        req.setAttribute("projects", projects);
        req.getRequestDispatcher("/WEB-INF/view/listAllProjects.jsp").forward(req, resp);

    }
}
