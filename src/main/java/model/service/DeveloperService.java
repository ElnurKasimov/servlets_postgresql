package model.service;

import model.dao.CompanyDao;
import model.dao.DeveloperDao;
import model.dao.ProjectDao;
import model.dto.*;
import model.service.converter.CompanyConverter;
import model.service.converter.DeveloperConverter;
import model.service.converter.ProjectConverter;
import model.storage.CompanyStorage;
import model.storage.DeveloperStorage;
import model.storage.ProjectStorage;
import model.storage.SkillStorage;


import java.util.*;

public class DeveloperService {
    private DeveloperStorage developerStorage;
    private ProjectService projectService;
    private ProjectStorage projectStorage;
    private SkillStorage skillStorage;
    private CompanyStorage companyStorage;
    private RelationService relationService;
    private SkillService skillService;

public DeveloperService (DeveloperStorage developerStorage, ProjectService projectService, ProjectStorage projectStorage,
                SkillStorage skillstorage, CompanyStorage companyStorage, RelationService relationService,  SkillService skillService) {
    this.developerStorage = developerStorage;
    this.projectService = projectService;
    this.projectStorage = projectStorage;
    this.skillStorage = skillstorage;
    this.companyStorage = companyStorage;
    this.relationService = relationService;
    this.skillService = skillService;
}

    public String saveDeveloper (String lastName, String firstName, int age, String companyName, int salary,
                                 String projectName, String language, String level) {
      String result = "";
      CompanyDto companyDto = null;
      ProjectDto projectDto = null;
      DeveloperDto savedDeveloper = new DeveloperDto();
      DeveloperDto developerDtoToSave = new DeveloperDto();
      developerDtoToSave.setLastName(lastName);
      developerDtoToSave.setFirstName(firstName);
      developerDtoToSave.setAge(age);
      developerDtoToSave.setSalary(salary);
      if (companyStorage.findByName(companyName).isPresent()) {
          developerDtoToSave.setCompanyDto(CompanyConverter.from(companyStorage.findByName(companyName).get()));
          DeveloperDto developerFromDb = DeveloperConverter.from(developerStorage.findByName(lastName, firstName).get());
          if (!validateByName(developerDtoToSave, developerFromDb).equals("")) {
              if (projectStorage.findByName(projectName).isPresent()) {
                  //todo реализовать ввод нескольких проектов для разработчика в jsp
                  // проверять по каждому проекту есть ли он в этой компании

                  projectDto = ProjectConverter.from(projectStorage.findByName(projectName).get());
                  savedDeveloper = DeveloperConverter.from(developerStorage.save(DeveloperConverter.to(developerDtoToSave)));
                  relationService.saveProjectDeveloper(projectDto, savedDeveloper);
                  relationService.saveDeveloperSkill(newDeveloperDto, skillsDto);
                  //todo
                  // 1. save developer
                  // 2. save project_developer relation
                  // 3. save developer_skill relation

                  "\tDeveloper " + developerDto.getLastName() + " " +
                          developerDto.getFirstName() + " successfully added to the database";


                  Optional<ProjectDao> projectFromDb =
                          projectStorage.findByName(newProjectDto.getProject_name());
                  if (projectFromDb.isPresent()) {
                      if (validateByName(newProjectDto, ProjectConverter.from(projectFromDb.get()))) {
                          savedDeveloper = ProjectConverter.from(projectFromDb.get()); // with id
                      } else {
                          result = (String.format("\tProject with name '%s ' already exist with different another data." +
                                  " Please enter correct data", newProjectDto.getProject_name()));
                      }
                  } else {
                      savedDeveloper = ProjectConverter.from(projectStorage.save(ProjectConverter.to(newProjectDto))); // with id
                      result = "Project " + newProjectDto.getProject_name() + " successfully added to the database";
                  }
              } else {
                  result = String.format("The company '%s' doesn't develop project with name '%s'. Please enter correct data.",
                          companyName, projectName);
              }
          } else {
              result = validateByName(developerDtoToSave, developerFromDb);
          }
      } else {
          result = "There is no company with name '" + companyName + "' in the database. Please enter correct data.";
      }

      return result;
    }

    public String validateByName(DeveloperDto developerDto, DeveloperDto developerFromDb) {
      if( (developerDto.getAge() == developerFromDb.getAge()) &&
          (developerDto.getCompanyDto().getCompany_name().equals(developerFromDb.getCompanyDto().getCompany_name() ) )
          && (developerDto.getSalary() == developerFromDb.getSalary()) ) {
            return "";
        } else return   String.format("\tDeveloper with name '%s %s ' already exist with different another data." +
                         " Please enter correct data", developerDto.getLastName(), developerDto.getFirstName());
    }

    public List<DeveloperDto> findAllDevelopers() {
         return  developerStorage.findAll()
                .stream().map(Optional::get)
                .map(DeveloperConverter::from)
                .toList();
    }

    public DeveloperDto getByName(String lastName, String firstName) {
        return developerStorage.findByName(lastName, firstName).map(DeveloperConverter::from).orElse(null);
    }

    public void getInfoByName(String lastName, String firstName) {
        List<String> result = new ArrayList<>();
        DeveloperDto developerDto = DeveloperConverter.from(developerStorage.findByName(lastName, firstName).get());
        result.add(String.format("\t\tDeveloper  %s %s  :", developerDto.getLastName(), developerDto.getFirstName()));
        result.add("\t\t\tAge : " + developerDto.getAge() + ",");
        result.add(String.format("\t\t\tWorks in company %s, with salary %d",
                             developerDto.getCompanyDto().getCompany_name(), developerDto.getSalary()));
        StringBuilder projectsName = new StringBuilder();
        projectsName.append("\t\t\tParticipates in such projects :");
        List<String> projectsList = projectStorage.getProjectsNameByDeveloperId(developerDto.getDeveloper_id());
        for ( String project : projectsList) {
            projectsName.append(" " + project + ",");
        }
        projectsName.deleteCharAt(projectsName.length()-1);
        result.add(projectsName.toString());
        StringBuilder skillsName = new StringBuilder();
        skillsName.append("\t\t\tHas skill set :");
        List<String> skillsList =  skillStorage.getSkillSetByDeveloperId(developerDto.getDeveloper_id());
        for ( String skill : skillsList) {
            skillsName.append(" " + skill + ",");
        }
        skillsName.deleteCharAt(skillsName.length()-1);
        result.add(skillsName.toString());
       // Output.getInstance().print(result);
    };

    public boolean isExist(String lastName, String firstName) {
        return developerStorage.isExist(lastName, firstName);
    }

    public List<String> getListNamesDevelopersWithCertainLanguage(String language) {
        return  developerStorage.getNamesListOfCertainLanguageDevelopers(language);
    };

    public List<String> getListNamesDevelopersWithCertainLevel(String level) {
        return  developerStorage.getNamesListOfCertainLevelDevelopers(level);
    };

    public List<String> getDevelopersNamesByProjectName(String projectName) {
        return developerStorage.getDevelopersNamesByProjectName(projectName);
    }

    public void updateDeveloper() {
        System.out.println("\tEnter, please,  data for developer You want to update.");
        DeveloperDto developerDtoToUpdate;
        String lastName;
        String firstName;
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.print("\tLast name : ");
            lastName = sc.nextLine();
            System.out.print("\tFirst name : ");
            firstName = sc.nextLine();
            Optional<DeveloperDao> currentDeveloperDao = developerStorage.findByName(lastName, firstName);
            if(currentDeveloperDao.isPresent()) {
                developerDtoToUpdate = DeveloperConverter.from(currentDeveloperDao.get());
                break;
            }
            System.out.println("There is no such developer in the database. Please enter correct data");
        }
        System.out.print("\tEnter new age (only digits) or just click 'Enter' if this field will not be changed : ");
        String newAgeString = sc.nextLine();
        int newAge;
        if(newAgeString.equals("")) {
            newAge = developerDtoToUpdate.getAge();
        } else {
            newAge = Integer.parseInt(newAgeString);
        }
        System.out.print("\tEnter new salary(only digits) or just click 'Enter' if this field will not be changed : ");
        String newSalaryString = sc.nextLine();
        int newSalary;
        if(newSalaryString.equals("")) {
            newSalary = developerDtoToUpdate.getSalary();
        } else {
            newSalary = Integer.parseInt(newSalaryString);
        }
        CompanyDto newCompany;
        System.out.print("\tEnter name of  new company where he works or just click 'Enter' if this field will not be changed: ");
        String companyName;
        while (true) {
            companyName = sc.nextLine();
            if(companyName.equals("")) {
                newCompany = developerDtoToUpdate.getCompanyDto();
                break;
            } else {
                Optional<CompanyDao> companyDaoFromDB = companyStorage.findByName(companyName);
                if (companyDaoFromDB.isPresent()) {
                    newCompany = CompanyConverter.from(companyDaoFromDB.get());
                    break;
                }
            }
            System.out.print("Unfortunately, there is no company with such name in the database.  Enter correct data or add such company to database.");
        }
        developerDtoToUpdate = new DeveloperDto(lastName, firstName, newAge, newCompany, newSalary);
        DeveloperDto updatedDeveloperDto = DeveloperConverter.from(developerStorage.update(DeveloperConverter.to(developerDtoToUpdate)));
        Set<ProjectDto> newProjectsDto = projectService.checkByCompanyName(newCompany.getCompany_name());
        relationService.deleteAllProjectsOfDeveloper(updatedDeveloperDto);
        relationService.saveProjectDeveloper(newProjectsDto, updatedDeveloperDto);
        Set<SkillDto> newSkillsDto = new HashSet<>();
        while (true) {
            System.out.print("\tLanguage the developer operated  : ");
            String language = sc.nextLine();
            System.out.print("\tLevel knowledge of the language (junior, middle, senior) : ");
            String level = sc.nextLine();
            SkillDto skillDto = skillService.findByLanguageAndLevel(language, level);
            newSkillsDto.add(skillDto);
            System.out.print("One more language? (yes/no) : ");
            String anotherLanguage = sc.nextLine();
            if (anotherLanguage.equalsIgnoreCase("no")) break;
        }
        relationService.deleteAllSkillsOfDeveloper(updatedDeveloperDto);
        relationService.saveDeveloperSkill(updatedDeveloperDto, newSkillsDto);
        List<String> result = new ArrayList<>();
        result.add(String.format("Developer %s %s successfully updated.",
                updatedDeveloperDto.getLastName(), updatedDeveloperDto.getFirstName()));
       // Output.getInstance().print(result);
    }

    public void deleteDeveloper() {
        System.out.println("\tEnter, please,  data for developer You want to delete.");
        DeveloperDto developerDtoToDelete;
        String lastName;
        String firstName;
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\tLast name : ");
            lastName = sc.nextLine();
            System.out.print("\tFirst name : ");
            firstName = sc.nextLine();
            Optional<DeveloperDao> developerDaofromDb = developerStorage.findByName(lastName, firstName);
            if (developerDaofromDb.isPresent()) {
                developerDtoToDelete = DeveloperConverter.from(developerDaofromDb.get());
                break;
            }
            System.out.println("There is no such developer in the database. Please enter correct data");
        }
        relationService.deleteDeveloperFromDeveloperSkill(developerDtoToDelete);
        relationService.deleteDeveloperFromProjectDeveloper(developerDtoToDelete);
        developerStorage.delete(DeveloperConverter.to(developerDtoToDelete));
        List<String> result = new ArrayList<>();
        result.add(String.format("Developer %s %s successfully deleted from the database.",
                developerDtoToDelete.getLastName(), developerDtoToDelete.getFirstName()));
       // Output.getInstance().print(result);
    }

}

