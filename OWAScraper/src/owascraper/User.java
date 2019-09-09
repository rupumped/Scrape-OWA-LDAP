package owascraper;

import java.util.Scanner;

public class User {
    private User() { }
    
    public static User parseUser(String str) throws UnknownFieldException {
        User user = new User();
        Scanner scanner = new Scanner(str);
        
        // Parse user info
        user.name = scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            
            // Remove Header
            if (line.startsWith("Contact")) {
                line = line.substring(7);
            } else if (line.startsWith("Information")) {
                line = line.substring(11);
            }
            
            // Use label to sort data
            if (line.startsWith("Alias")) {
                user.alias = line.substring(5);
            } else if (line.startsWith("E-mail")) {
                user.email = line.substring(6);
            } else if (line.startsWith("Office")) {
                user.office = line.substring(6);
            } else if (line.startsWith("Phone")) {
                user.phone = line.substring(5);
            } else if (line.startsWith("Mobile phone")) {
                if (user.phone == null)
                    user.phone = line.substring(12);
                else
                    user.phone += ";" + line.substring(12);
            } else if (line.startsWith("Home phone")) {
                if (user.phone == null)
                    user.phone = line.substring(10);
                else
                    user.phone += ";" + line.substring(10);
            } else if (line.startsWith("Job title")) {
                user.job_title = line.substring(9);
            } else if (line.startsWith("Department")) {
                user.department = line.substring(10);
            } else if (line.startsWith("Company")) {
                user.company = line.substring(7);
            } else if (line.startsWith("Fax")) {
            } else if (line.startsWith("Organization")) {
                return user;
//            } else if (line.startsWith("Manager")) {
//                user.manager = line.substring(7);
//            } else if (line.startsWith("Assistant")) {
//                user.assistant = line.substring(10);
//            } else if (line.startsWith("IM address")) {
//                user.im_address = line.substring(10);
//            } else if (line.startsWith("Web page")) {
//                user.web_page = line.substring(8);
            } else {
                throw new UnknownFieldException(line);
            }
        }
        return user;
    }
    
    @Override
    public String toString() {
        return this.name
               + "\n\tAlias: " + this.alias
               + "\n\tJob Title: " + this.job_title
               + "\n\tOffice: " + this.office
               + "\n\tDepartment: " + this.department
               + "\n\tCompany: " + this.company
               + "\n\tManager: " + this.manager
               + "\n\tAssistant: " + this.assistant
               + "\n\tPhone: " + this.phone
               + "\n\tE-mail: " + this.email
               + "\n\tIM Address: " + this.im_address
               + "\n\tWeb Page: " + this.web_page;
    }
    
    public String toCSV() {
        String csv = this.name + ",";
        if (this.alias != null) csv+= this.alias;
        csv+= ",";
        if (this.job_title != null) csv+= this.job_title;
        csv+= ",";
        if (this.office != null) csv+= this.office;
        csv+= ",";
        if (this.department != null) csv+= this.department;
        csv+= ",";
        if (this.phone != null) csv+= this.phone;
        csv+= ",";
        if (this.email != null) csv+= this.email;
        csv+= ",";
        
        return csv;
    }
    
    class Address {
        String street;
        String city;
        String state;
        String postal_code;
        String country;
    }
    
    String alias;
    String name;
    String job_title;
    String office;
    String department;
    String company;
    String manager;
    String assistant;
    String phone;
    String email;
    String im_address;
    String web_page;
    Address business;
    Address home;
    Address other;
}