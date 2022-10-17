package model.service;

import model.dao.CustomerDao;
import model.dto.CustomerDto;
import model.service.converter.CustomerConverter;
import model.storage.CustomerStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CustomerService {
    private CustomerStorage customerStorage;

public  CustomerService (CustomerStorage customerStorage) {
    this.customerStorage = customerStorage;
}


    public String save (CustomerDto customerDto) {
        String result = "";
        Optional<CustomerDao> customerFromDb = customerStorage.findByName(customerDto.getCustomer_name());
        if (customerFromDb.isPresent()) {
            result = validateByName(customerDto, CustomerConverter.from(customerFromDb.get()));
        } else {
            customerStorage.save(CustomerConverter.to(customerDto));
            result = ("Customer " + customerDto.getCustomer_name() + "successfully added to the database");
        };
        return result;
    }

    public String  validateByName(CustomerDto customerDto, CustomerDto customerFromDb) {
        if (!customerDto.getReputation().toString().equals(customerFromDb.getReputation().toString())) {
            return String.format("Customer with name '%s' already exist with different " +
                            "reputation '%s'. Please enter correct data",
                    customerDto.getCustomer_name(), customerFromDb.getReputation().toString());
        } else return "Ok. A customer with such parameters is present in the database already.";
    }

    public Optional<CustomerDto> findByName(String name) {
        Optional<CustomerDao> customerDaoFromDb = customerStorage.findByName(name);
        return customerDaoFromDb.map(CustomerConverter::from);
    }

    public List<CustomerDto> findAllCustomers() {
        return customerStorage.findAll()
                .stream().map(Optional::get)
                .map(CustomerConverter::from)
                .toList();
    }

    public void deleteCustomer (String name) {
        List<String> result = new ArrayList<>();
        customerStorage.delete(customerStorage.findByName(name).get());
        result.add("Customer " + name + " successfully deleted from the database");
       // Output.getInstance().print(result);
    }

    public void updateCustomer() {
        System.out.print("Enter name of the customer to update : ");
        Scanner sc = new Scanner(System.in);
        CustomerDto customerDtoToUpdate = null;
        while (true) {
            String name = sc.nextLine();
            Optional<CustomerDto>  customerDtoFromDb = findByName(name);
            if (customerDtoFromDb.isEmpty()) {
                System.out.print("Unfortunately, there is no customer with such name in the database.  Please enter correct customer name : ");
            } else {
                customerDtoToUpdate = customerDtoFromDb.get();
                break;
            }
        }
        System.out.print("Enter new reputation for the customer  (respectable, trustworthy, insolvent) : ");
        String newCustomerReputation = sc.nextLine();
        customerDtoToUpdate.setReputation(CustomerDto.Reputation.valueOf(newCustomerReputation));
        CustomerDto updatedCustomerDto = CustomerConverter.from(customerStorage.update(CustomerConverter.to(customerDtoToUpdate)));
        List<String> result = new ArrayList<>();
        result.add(String.format("Customer %s successfully updated.", updatedCustomerDto.getCustomer_name()));
      //  Output.getInstance().print(result);
    }
}
