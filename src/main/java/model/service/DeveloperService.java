package model.service;

import model.dao.DeveloperDao;
import model.dto.*;
import model.service.converter.CompanyConverter;
import model.service.converter.DeveloperConverter;
import model.storage.CompanyStorage;
import model.storage.DeveloperStorage;
import model.storage.ProjectStorage;
import model.storage.SkillStorage;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeveloperService {
    private DeveloperStorage developerStorage;
    private ProjectService projectService;
    private ProjectStorage projectStorage;
    private SkillStorage skillStorage;
    private CompanyStorage companyStorage;
    private RelationService relationService;
    private SkillService skillService;

    public DeveloperService(DeveloperStorage developerStorage, ProjectService projectService, ProjectStorage projectStorage,
                            SkillStorage skillstorage, CompanyStorage companyStorage, RelationService relationService, SkillService skillService) {
        this.developerStorage = developerStorage;
        this.projectService = projectService;
        this.projectStorage = projectStorage;
        this.skillStorage = skillstorage;
        this.companyStorage = companyStorage;
        this.relationService = relationService;
        this.skillService = skillService;
    }

    public String saveDeveloper(String lastName, String firstName, int age, String companyName, int salary) {
        String result = "";
        CompanyDto companyDto = null;
        DeveloperDto developerDtoToSave = new DeveloperDto();
        developerDtoToSave.setLastName(lastName);
        developerDtoToSave.setFirstName(firstName);
        developerDtoToSave.setAge(age);
        developerDtoToSave.setSalary(salary);
        if (companyStorage.findByName(companyName).isPresent()) {
            developerDtoToSave.setCompanyDto(CompanyConverter.from(companyStorage.findByName(companyName).get()));
            Optional<DeveloperDao> developerFromDb = developerStorage.findByName(lastName, firstName);
            if (developerFromDb.isPresent()) {
                result = validateByName(developerDtoToSave, DeveloperConverter.from(developerFromDb.get()));
            } else {
                DeveloperDto savedDeveloperDto = DeveloperConverter.from(developerStorage.save(DeveloperConverter.to(developerDtoToSave)));
            }
        } else {
            result = "There is no company with name '" + companyName + "' in the database. Please enter correct data.";
        }
        return result;
    }

    public String saveDeveloperRelations(DeveloperDto developerDto, Set<ProjectDto> developerProjects, String language, String level) {
        List<Long> projectIdsByDeveloperIdFromDb = projectService.getProjectIdsByDeveloperId(developerDto.getDeveloper_id());
        Set<ProjectDto> newDeveloperProjects = developerProjects.stream()
                .filter(project -> !projectIdsByDeveloperIdFromDb.contains(project.getProject_id()))
                .collect(Collectors.toSet());
        if (!newDeveloperProjects.isEmpty()) {
            relationService.saveProjectDeveloper(developerProjects, developerDto);
        }

        Set<SkillDto> developerSkills = new HashSet<>();
        developerSkills.add(skillService.findByLanguageAndLevel(language, level));

        List<Long> developerSkillIdsFromDb = skillService.getSkillIdsByDeveloperId(developerDto.getDeveloper_id());
        Set<SkillDto> newDeveloperSkills = developerSkills.stream()
                .filter(skill -> !developerSkillIdsFromDb.contains(skill.getSkill_id()))
                .collect(Collectors.toSet());
        if (!newDeveloperSkills.isEmpty()) {
            relationService.saveDeveloperSkill(developerDto, newDeveloperSkills);
        }
        return String.format("Developer %s %s successfully added into database with all necessary relations."
                , developerDto.getLastName(), developerDto.getFirstName());
    }

    public String validateByName(DeveloperDto developerDto, DeveloperDto developerFromDb) {
        if ((developerDto.getAge() == developerFromDb.getAge()) &&
                (developerDto.getCompanyDto().getCompany_name().equals(developerFromDb.getCompanyDto().getCompany_name()))
                && (developerDto.getSalary() == developerFromDb.getSalary())) {
            return "";
        } else return String.format("\tDeveloper  %s %s  already exists with different another data." +
                " Please enter correct data", developerDto.getLastName(), developerDto.getFirstName());
    }

    public List<DeveloperDto> findAllDevelopers() {
        return developerStorage.findAll()
                .stream().map(Optional::get)
                .map(DeveloperConverter::from)
                .toList();
    }

    public DeveloperDto getByName(String lastName, String firstName) {
        return developerStorage.findByName(lastName, firstName).map(DeveloperConverter::from).orElse(null);
    }

    public long getIdByName(String lastName, String firstName) {
        return developerStorage.getIdByName(lastName, firstName);
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
        for (String project : projectsList) {
            projectsName.append(" " + project + ",");
        }
        projectsName.deleteCharAt(projectsName.length() - 1);
        result.add(projectsName.toString());
        StringBuilder skillsName = new StringBuilder();
        skillsName.append("\t\t\tHas skill set :");
        List<String> skillsList = skillStorage.getSkillSetByDeveloperId(developerDto.getDeveloper_id());
        for (String skill : skillsList) {
            skillsName.append(" " + skill + ",");
        }
        skillsName.deleteCharAt(skillsName.length() - 1);
        result.add(skillsName.toString());

    }

    ;

    public boolean isExist(String lastName, String firstName) {
        return developerStorage.isExist(lastName, firstName);
    }

    public List<String> getListNamesDevelopersWithCertainLanguage(String language) {
        return developerStorage.getNamesListOfCertainLanguageDevelopers(language);
    }

    ;

    public List<String> getListNamesDevelopersWithCertainLevel(String level) {
        return developerStorage.getNamesListOfCertainLevelDevelopers(level);
    }

    ;

    public List<String> getDevelopersNamesByProjectName(String projectName) {
        return developerStorage.getDevelopersNamesByProjectName(projectName);
    }

    public String findDeveloperForUpdate(String lastName, String firstName) {
        return developerStorage.findByName(lastName, firstName).isPresent() ?
                "" : "There is no developer with such name in the database. Please, input correct data.";
    }

    public String updateDeveloper(DeveloperDto developerDtoToUpdate, String[] projectsNames, Set<SkillDto> skillsDto) {

        DeveloperDto updatedDeveloperDto = DeveloperConverter.from(developerStorage.update(DeveloperConverter.to(developerDtoToUpdate)));
        Set<ProjectDto> projects = Stream.of(projectsNames)
                        .map(name -> projectService.findByName(name).get())
                        .collect(Collectors.toSet());
        relationService.deleteAllProjectsOfDeveloper(updatedDeveloperDto);
        relationService.saveProjectDeveloper(projects, updatedDeveloperDto);

        relationService.deleteAllSkillsOfDeveloper(updatedDeveloperDto);
        relationService.saveDeveloperSkill(updatedDeveloperDto, skillsDto);

        return  String.format("Developer %s %s successfully updated with all necessary relations.",
                updatedDeveloperDto.getLastName(), updatedDeveloperDto.getFirstName());
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

    }

}

