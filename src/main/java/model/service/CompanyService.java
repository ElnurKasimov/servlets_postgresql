package model.service;

import model.dao.CompanyDao;
import model.dto.CompanyDto;
import model.service.converter.CompanyConverter;
import model.storage.CompanyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CompanyService {
    private CompanyStorage companyStorage;

public  CompanyService (CompanyStorage companyStorage) {
    this.companyStorage = companyStorage;
}

public CompanyDto save (CompanyDto companyDto) {
    List<String> result = new ArrayList<>();
    Optional<CompanyDao> companyFromDb = companyStorage.findByName(companyDto.getCompany_name());
    if (companyFromDb.isPresent()) {
        result.add(validateByName(companyDto, CompanyConverter.from(companyFromDb.get())));
    } else {
        companyStorage.save(CompanyConverter.to(companyDto));
        result.add("\tCompany " + companyDto.getCompany_name() + " successfully added to the database");
    };
  //  Output.getInstance().print(result);
    return companyDto;
}

    public String  validateByName(CompanyDto companyDto, CompanyDto companyFromDb) {
        if (!companyDto.getRating().toString().equals(companyFromDb.getRating().toString())) {
            return String.format("\tCompany with name '%s' already exist with different " +
                    "rating '%s'. Please enter correct data",
                    companyDto.getCompany_name(), companyFromDb.getRating().toString());
        } else return "Ok. The company is present in the database";
    }

    public List<CompanyDto> findAllCompanies() {
        return companyStorage.findAll()
                .stream().map(Optional::get)
                .map(CompanyConverter::from)
                .toList();
    }

    public Optional<CompanyDto> findById(long id) {
    Optional<CompanyDao> companyDaoFromDb = companyStorage.findById(id);
    return companyDaoFromDb.map(CompanyConverter::from);
    }

    public Optional<CompanyDto> findByName(String name) {
        Optional<CompanyDao> companyDaoFromDb = companyStorage.findByName(name);
        return companyDaoFromDb.map(CompanyConverter::from);
    }

    public CompanyDto createCompany() {
        System.out.print("Enter company name : ");
        Scanner sc = new Scanner(System.in);
        String newCompanyName = sc.nextLine();
        System.out.print("Enter company rating (high, middle, low) : ");
        String newCompanyRating = sc.nextLine();
        return new CompanyDto(newCompanyName, CompanyDto.Rating.valueOf(newCompanyRating));
    }

    public CompanyDto checkByName (String name) {
        CompanyDto companyDto = findByName(name).orElseGet(this::createCompany);
        return  save(companyDto);
    }

    public void deleteCompany (String name) {
        List<String> result = new ArrayList<>();
        companyStorage.delete(companyStorage.findByName(name).get());
        result.add("Company " + name + " successfully deleted from the database");
      //  Output.getInstance().print(result);
    }

    public void updateCompany() {
        System.out.print("Enter name of the company to update : ");
        Scanner sc = new Scanner(System.in);
        CompanyDto companyDtoToUpdate = null;
        while (true) {
            String name = sc.nextLine();
            Optional<CompanyDto>  companyDtoFromDb = findByName(name);
            if (companyDtoFromDb.isEmpty()) {
                System.out.print("Unfortunately, there is no company with such name in the database.  Please enter correct company name : ");
            } else {
                companyDtoToUpdate = companyDtoFromDb.get();
                break;
            }
        }
        System.out.print("Enter new rating for the company (high, middle, low) : ");
        String newCompanyRating = sc.nextLine();
        companyDtoToUpdate.setRating(CompanyDto.Rating.valueOf(newCompanyRating));
        CompanyDto updatedCompanyDto = CompanyConverter.from(companyStorage.update(CompanyConverter.to(companyDtoToUpdate)));
        List<String> result = new ArrayList<>();
        result.add(String.format("Company %s successfully updated.", updatedCompanyDto.getCompany_name()));
       // Output.getInstance().print(result);
    }

}
