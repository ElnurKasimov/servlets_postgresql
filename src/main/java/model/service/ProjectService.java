package model.service;

import model.dao.ProjectDao;
import model.dto.CompanyDto;
import model.dto.CustomerDto;
import model.dto.ProjectDto;
import model.service.converter.ProjectConverter;
import model.storage.DeveloperStorage;
import model.storage.ProjectStorage;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProjectService {
    private ProjectStorage projectStorage;
    private DeveloperStorage developerStorage;
    private CompanyService companyService;
    private CustomerService customerService;
    private RelationService relationService;

    public ProjectService(ProjectStorage projectStorage, DeveloperStorage developerStorage,
                          CompanyService companyService, CustomerService customerService,
                          RelationService relationService) {
        this.projectStorage = projectStorage;
        this.developerStorage = developerStorage;
        this.companyService = companyService;
        this.customerService = customerService;
        this.relationService = relationService;
    }

    public boolean isExist(String projectName) {
        return projectStorage.isExist(projectName);
    }

    public List<ProjectDto> findAllProjects() {
         return projectStorage.findAll().stream()
                .map(Optional::get)
                .map(ProjectDao::getProject_name)
                .map(name -> projectStorage.findByName(name))
                .map(project -> project.get())
                .map(ProjectConverter::from)
                .toList();
    }

    public List<String> getProjectsNameByDeveloperId(long id) {
        return projectStorage.getProjectsNameByDeveloperId(id);
    }

    public List<ProjectDto> getCompanyProjects(String companyName) {
        List<ProjectDto> projects = new ArrayList<>();
        List<ProjectDao> projectDaoList = projectStorage.getCompanyProjects(companyName);
        for (ProjectDao projectDao : projectDaoList) {
            projects.add(ProjectConverter.from(projectDao));
        }
        return projects;
    }

    public List<ProjectDto> getCustomerProjects(String customerName) {
        List<ProjectDto> projects = new ArrayList<>();
        List<ProjectDao> projectDaoList = projectStorage.getCustomerProjects(customerName);
        for (ProjectDao projectDao : projectDaoList) {
            projects.add(ProjectConverter.from(projectDao));
        }
        return projects;
    }

    public long getIdByName(String name) {
        return projectStorage.getIdByName(name).orElseGet(() -> {
            System.out.print("There is no project with such name. Please enter correct data");
            return (long) 0;
        });
    }

    public Set<ProjectDto> checkByCompanyName(String companyName) {
        System.out.printf("\tCompany %s develops such projects : \n", companyName);
        List<ProjectDto> projectDtoList = getCompanyProjects(companyName);
        for (ProjectDto projectDto : projectDtoList) {
            System.out.print(projectDto.getProject_name() + ",\n");
        }
        if (projectDtoList.isEmpty()) {
            System.out.println("\nThere is no project in the company. Please create the one.");
            ProjectDto newProjectDto;
            do {
                newProjectDto = createProject();
                newProjectDto = save(newProjectDto);
            } while (newProjectDto.getProject_id() == 0);
            System.out.println("Company " + companyName + " develops project " + newProjectDto.getProject_name());
        }
        Set<ProjectDto> projectsDto = new HashSet<>();
        while (true) {
            System.out.print("\tPlease enter project name the developers participate in : ");
            Scanner sc = new Scanner(System.in);
            String projectName = sc.nextLine();
            Optional<ProjectDao> projectDaoFromDb = projectStorage.findByName(projectName);
            if (projectDaoFromDb.isPresent()) {
                ProjectDto selectedProject = ProjectConverter.from(projectDaoFromDb.get());
                projectsDto.add(selectedProject);
                System.out.print("One more project? (yes/no) : ");
                String anotherLanguage = sc.nextLine();
                if (anotherLanguage.equalsIgnoreCase("no")) break;
            } else {
                System.out.println("\tThere is no such project. Please enter correct data");
            }
        }
        return projectsDto;
    }

    public ProjectDto createProject() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\tEnter name of the project : ");
        String projectName = sc.nextLine();
        CompanyDto companyDto = null;
        CustomerDto customerDto = null;
        while (true) {
            System.out.print("\tEnter company name which develops the project : ");
            String company = sc.nextLine();
            if (companyService.findByName(company).isPresent()) {
                companyDto = companyService.findByName(company).get();
                break;
            } else {
                System.out.println("There is no company with such name. Please enter correct one.");
            }
        }
        while (true) {
            System.out.print("\tEnter customer name which ordered the development of the project : ");
            String customer = sc.nextLine();
            if (customerService.findByName(customer).isPresent()) {
                customerDto = customerService.findByName(customer).get();
                break;
            } else {
                System.out.println("There is no customer with such name. Please enter correct one.");
            }
        }
        System.out.print("\tBudget of the project (only digits): ");
        int cost = Integer.parseInt(sc.nextLine());
        System.out.print("\tStart date of the project (in format yyyy-mm-dd): ");
        String startDate = sc.nextLine();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        LocalDate startLocalDate = LocalDate.parse(startDate, dtf);
        java.sql.Date startSqlDate = java.sql.Date.valueOf(startLocalDate);
        return new ProjectDto(projectName, companyDto, customerDto, cost, startSqlDate);
    }

    public ProjectDto save(ProjectDto projectDto) {
        List<String> stringList = new ArrayList<>();
        Optional<ProjectDao> projectFromDb =
                projectStorage.findByName(projectDto.getProject_name());
        ProjectDto result = new ProjectDto();
        if (projectFromDb.isPresent()) {
            if (validateByName(projectDto, ProjectConverter.from(projectFromDb.get()))) {
                result = ProjectConverter.from(projectFromDb.get()); // with id
            } else {
                stringList.add(String.format("\tProject with name '%s ' already exist with different another data." +
                        " Please enter correct data", projectDto.getProject_name()));
                result = projectDto; // without id
            }
        } else {
            stringList.add("\tProject " + projectDto.getProject_name() + " successfully added to the database");
            result = ProjectConverter.from(projectStorage.save(ProjectConverter.to(projectDto))); // with id
        }
       // Output.getInstance().print(stringList);
        return result;
    }

    public boolean validateByName(ProjectDto projectDto, ProjectDto projectFromDb) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateFromProjectDto = dateFormat.format(projectDto.getStart_date());
        String dateFromProjectFromDb = dateFormat.format(projectFromDb.getStart_date());
        return (projectDto.getCompanyDto().getCompany_name().equals(projectFromDb.getCompanyDto().getCompany_name())) &&
                (projectDto.getCustomerDto().getCustomer_name().equals(projectFromDb.getCustomerDto().getCustomer_name())) &&
                (projectDto.getCost() == projectFromDb.getCost()) &&
                (dateFromProjectDto.equals(dateFromProjectFromDb));
    }

    public  ProjectDto getInfoByName(String name) {
        return ProjectConverter.from(projectStorage.findByName(name).get());
    }

    public List<String> getDevelopersNamesByProjectName(String name) {
        return developerStorage.getDevelopersNamesByProjectName(name);
    }

    public long getProjectExpences(String projectName) {
        return  projectStorage.getProjectExpences(projectName);
    }

    public List<String> getProjectsListInSpecialFormat() {
        List<String> result = new ArrayList<>();
        if (!projectStorage.findAll().isEmpty()) {
            projectStorage.findAll().forEach(projectDao ->
                    result.add(String.format("%s - %s - %d,",
                            projectDao.get().getStart_date().toString(),
                            projectDao.get().getProject_name(),
                            developerStorage.getQuantityOfProjectDevelopers(projectDao.get().getProject_name())
                    )));
        }
     return result;
    }

    public void updateProject() {
        Scanner sc = new Scanner(System.in);
        ProjectDto currentProjectDto = null;
        String newProjectName;
        while (true) {
            System.out.print("\tEnter name of the project You want to update: ");
            newProjectName = sc.nextLine();
            Optional<ProjectDao> currentProjectDao = projectStorage.findByName(newProjectName);
            if (currentProjectDao.isPresent()) {
                currentProjectDto = ProjectConverter.from(currentProjectDao.get());
                break;
            }
            System.out.println("There is no such project in the database. Please enter correct data");
        }
        CompanyDto newCompanyDto = null;
        CustomerDto newCustomerDto = null;
        while (true) {
            System.out.print("\tEnter new company name which develops the project or just click 'Enter' if this field will not be changed : ");
            String newCompanyName = sc.nextLine();
            if (newCompanyName.equals("")) {
                newCompanyDto = currentProjectDto.getCompanyDto();
                break;
            }
            if (companyService.findByName(newCompanyName).isPresent()) {
                newCompanyDto = companyService.findByName(newCompanyName).get();
                break;
            }
            System.out.println("There is no company with such name. Please enter correct one.");
        }
        while (true) {
            System.out.print("\tEnter new customer name which ordered the development of the project or just click 'Enter' if this field will not be changed : ");
            String newCustomerName = sc.nextLine();
            if (newCustomerName.equals("")) {
                newCustomerDto = currentProjectDto.getCustomerDto();
                break;
            }
            if (customerService.findByName(newCustomerName).isPresent()) {
                newCustomerDto = customerService.findByName(newCustomerName).get();
                break;
            }
            System.out.println("There is no customer with such name. Please enter correct one.");
        }
        System.out.print("\tEnter new budget of the project (only digits) or just click 'Enter' if this field will not be changed : ");
        String costString = sc.nextLine();
        int newCost;
        if (costString.equals("")) {
            newCost = currentProjectDto.getCost();
        } else {
            newCost = Integer.parseInt(costString);
        }
        System.out.print("\tEnter new start date of the project (in format yyyy-mm-dd) or just click 'Enter' if this field will not be changed : ");
        String newStartDateString = sc.nextLine();
        java.sql.Date newStartSqlDate;
        if (newStartDateString.equals("")) {
            newStartSqlDate = currentProjectDto.getStart_date();
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            LocalDate newStartLocalDate = LocalDate.parse(newStartDateString, dtf);
            newStartSqlDate = java.sql.Date.valueOf(newStartLocalDate);
        }
        ProjectDto projectDtoToUpdate = new ProjectDto(newProjectName, newCompanyDto, newCustomerDto, newCost, newStartSqlDate);
        ProjectDto updatedProjectDto = ProjectConverter.from(projectStorage.update(ProjectConverter.to(projectDtoToUpdate)));
        List<String> result = new ArrayList<>();
        result.add(String.format("Project %s successfully updated.", updatedProjectDto.getProject_name()));
       // Output.getInstance().print(result);
    }

    public void deleteProject() {
        Scanner sc = new Scanner(System.in);
        ProjectDto projectDtoToDelete = null;
        String projectName;
        while (true) {
            System.out.print("\tEnter name of the project You want to update: ");
            projectName = sc.nextLine();
            Optional<ProjectDao> projectDaoFromDb = projectStorage.findByName(projectName);
            if (projectDaoFromDb.isPresent()) {
                projectDtoToDelete = ProjectConverter.from(projectDaoFromDb.get());
                break;
            }
            System.out.println("There is no such project in the database. Please enter correct data");
        }
        relationService.deleteAllDevelopersOfProject(projectDtoToDelete);
        projectStorage.delete(ProjectConverter.to(projectDtoToDelete));
        List<String> result = new ArrayList<>();
        result.add(String.format("Project %s successfully deleted from the database.", projectDtoToDelete.getProject_name()));
        //Output.getInstance().print(result);
    }

}
