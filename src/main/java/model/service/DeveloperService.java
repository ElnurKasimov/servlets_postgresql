package model.service;

import model.dao.CompanyDao;
import model.dao.DeveloperDao;
import model.dto.CompanyDto;
import model.dto.DeveloperDto;
import model.dto.ProjectDto;
import model.dto.SkillDto;
import model.service.converter.CompanyConverter;
import model.service.converter.DeveloperConverter;
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

    public DeveloperDto save (DeveloperDto developerDto) {
        List<String> result = new ArrayList<>();
        Optional<DeveloperDao> developerFromDb =
                developerStorage.findByName(developerDto.getLastName(), developerDto.getFirstName());
        DeveloperDao savedDeveloperWithId = new DeveloperDao();
        if(developerFromDb.isPresent()) {
            result.add(validateByName(developerDto, DeveloperConverter.from(developerFromDb.get())));
        } else {
            savedDeveloperWithId = developerStorage.save(DeveloperConverter.to(developerDto));
            result.add("\tDeveloper " + developerDto.getLastName() + " " +
                    developerDto.getFirstName() + " successfully added to the database");
        };
       // Output.getInstance().print(result);
        return DeveloperConverter.from(savedDeveloperWithId);
    }

    public String validateByName(DeveloperDto developerDto, DeveloperDto developerFromDb) {
      if( (developerDto.getAge() == developerFromDb.getAge()) &&
          (developerDto.getCompanyDto().getCompany_name().equals(developerFromDb.getCompanyDto().getCompany_name() ) )
          && (developerDto.getSalary() == developerFromDb.getSalary()) ) {
            return "\tDeveloper " + developerDto.getLastName() + " " +
                    developerDto.getFirstName() + " successfully added to the database";
        } else return   String.format("\tDeveloper with name '%s %s ' already exist with different another data." +
                         " Please enter correct data", developerDto.getLastName(), developerDto.getFirstName());
    }

    public void findAllDevelopers() {
        List<String> result = new ArrayList<>();
        for (Optional<DeveloperDao> developerDao : developerStorage.findAll()) {
            developerDao.ifPresent(dao -> result.add(String.format("\t%d. %s %s, works in company %s",
                    dao.getDeveloper_id(),
                    dao.getLastName(),
                    dao.getFirstName(),
                    dao.getCompanyDao().getCompany_name())));
        }
       // Output.getInstance().print(result);
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

    public void getListNamesDevelopersWithCertainLanguage(String language) {
        List<String> result = new ArrayList<>();
        result.add(String.format("\tThere are such developers who program in '%s' in the database : ", language));
        developerStorage.getNamesListOfCertainLanguageDevelopers(language).forEach(name -> result.add("\t\t" + name + ","));
       // Output.getInstance().print(result);
    };

    public void getListNamesDevelopersWithCertainLevel(String level) {
        List<String> result = new ArrayList<>();
        result.add(String.format("\tThere are such developers who has '%s' position in the database: ", level));
        developerStorage.getNamesListOfCertainLevelDevelopers(level).forEach(name -> result.add("\t\t" + name + ","));
      //  Output.getInstance().print(result);
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

