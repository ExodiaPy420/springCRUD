package com.alexdavid.gestorcrud.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class SalesrepDto {

    @NotEmpty(message = "El nombre no puede estar vacío")
    private String fullName;

    @NotEmpty(message = "El correo no puede estar vacío")
    @Email
    private String email;

    private String phoneNum;

    @NotEmpty(message = "El estado debe indicarse")
    private String status;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    

}
