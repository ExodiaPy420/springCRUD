package com.alexdavid.gestorcrud.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CustomerDto {

    @NotEmpty(message = "El nombre completo es obligatorio.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    private String fullName;

    @NotEmpty(message = "El correo electrónico es obligatorio.")
    @Email(message = "Debe ser un formato de correo electrónico válido.")
    @Size(max = 100, message = "El correo no puede exceder los 100 caracteres.")
    private String email;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres.")
    private String phoneNum;

    @NotNull(message = "El ID del comercial es obligatorio.")
    private Integer salesrepId;

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

    public Integer getSalesrepId() {
        return salesrepId;
    }

    public void setSalesrepId(Integer salesrepId) {
        this.salesrepId = salesrepId;
    }
}
