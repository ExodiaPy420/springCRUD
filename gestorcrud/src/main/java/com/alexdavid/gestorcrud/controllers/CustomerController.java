package com.alexdavid.gestorcrud.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alexdavid.gestorcrud.models.Customer;
import com.alexdavid.gestorcrud.models.CustomerDto;
import com.alexdavid.gestorcrud.models.Salesrep;
import com.alexdavid.gestorcrud.repositories.CustomerRepository;
import com.alexdavid.gestorcrud.repositories.SalesrepRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private SalesrepRepository salesrepRepo;

    @GetMapping({"", "/"})
    public String getCustomers(Model model) {
        List<Customer> customers = customerRepo.findAllByOrderByIdDesc();
        model.addAttribute("customerlist", customers);
        return "customer/index";
    }

    @GetMapping("/create")
    public String createCustomerPage(Model model) {
        CustomerDto customerDto = new CustomerDto();
        model.addAttribute("customerDto", customerDto);
        List<Salesrep> salesreps = salesrepRepo.findAll(); 
        model.addAttribute("salesreplist", salesreps);
        return "customer/create";
    }

    @PostMapping("/create")
    public String createCustomer(@Valid @ModelAttribute CustomerDto customerDto, BindingResult result, Model model) {
        if (customerRepo.findByEmail(customerDto.getEmail()) != null) {
            result.addError(new FieldError("customerDto", "email", "Este correo electrónico ya está en uso."));
        }

        Salesrep salesrep = salesrepRepo.findById(customerDto.getSalesrepId()).orElse(null);
        if (salesrep == null) {
            result.addError(new FieldError("customerDto", "salesrepId", "Comercial no encontrado."));
        }

        if (result.hasErrors()) {
            List<Salesrep> salesreps = salesrepRepo.findAll();
            model.addAttribute("salesreplist", salesreps);
            return "customer/create";
        }

        Customer customer = new Customer();
        customer.setFullName(customerDto.getFullName());
        customer.setEmail(customerDto.getEmail());
        customer.setPhoneNum(customerDto.getPhoneNum());
        customer.setSalesrep(salesrep);

        customerRepo.save(customer);
        return "redirect:/customer";
    }

    @GetMapping("/edit")
    public String editCustomerPage(Model model, @RequestParam int id) {
        Customer customer = customerRepo.findById(id).orElse(null);
        if (customer == null) {
            return "redirect:/customer";
        }

        CustomerDto customerDto = new CustomerDto();
        customerDto.setFullName(customer.getFullName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setPhoneNum(customer.getPhoneNum());
        customerDto.setSalesrepId(customer.getSalesrep().getId());

        model.addAttribute("customer", customer); // Para mostrar el ID
        model.addAttribute("customerDto", customerDto);
        List<Salesrep> salesreps = salesrepRepo.findAll();
        model.addAttribute("salesreplist", salesreps);

        return "customer/edit";
    }

    @PostMapping("/edit")
    public String editCustomer(Model model, @RequestParam int id,
            @Valid @ModelAttribute CustomerDto customerDto, BindingResult result) {

        Customer customer = customerRepo.findById(id).orElse(null);
        if (customer == null) {
            return "redirect:/customer";
        }
        model.addAttribute("customer", customer); // Para mantener el ID en la vista si hay error

        // Verificar si el email ha cambiado y si el nuevo email ya existe para otro cliente
        Customer existingCustomerWithEmail = customerRepo.findByEmail(customerDto.getEmail());
        if (existingCustomerWithEmail != null && existingCustomerWithEmail.getId() != id) {
            result.addError(new FieldError("customerDto", "email", "Este correo electrónico ya está en uso por otro cliente."));
        }

        Salesrep salesrep = salesrepRepo.findById(customerDto.getSalesrepId()).orElse(null);
        if (salesrep == null) {
            result.addError(new FieldError("customerDto", "salesrepId", "Comercial no encontrado."));
        }

        if (result.hasErrors()) {
            List<Salesrep> salesreps = salesrepRepo.findAll();
            model.addAttribute("salesreplist", salesreps);
            return "customer/edit";
        }

        customer.setFullName(customerDto.getFullName());
        customer.setEmail(customerDto.getEmail());
        customer.setPhoneNum(customerDto.getPhoneNum());
        customer.setSalesrep(salesrep);

        customerRepo.save(customer);
        return "redirect:/customer";
    }

    @GetMapping("/delete")
    public String deleteCustomer(@RequestParam int id) {
        Customer customer = customerRepo.findById(id).orElse(null);
        if (customer != null) {
            // Considerar qué hacer con las facturas del cliente:
            // O borrarlas en cascada (ya configurado en la entidad)
            // O impedir el borrado si tiene facturas, o reasignarlas, etc.
            customerRepo.delete(customer);
        }
        return "redirect:/customer";
    }
}
