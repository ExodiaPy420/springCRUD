package com.alexdavid.gestorcrud.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alexdavid.gestorcrud.models.Salesrep;
import com.alexdavid.gestorcrud.models.SalesrepDto;
import com.alexdavid.gestorcrud.repositories.SalesrepRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/salesrep")
public class SalesrepController {

    //primero tenemos que requestear el repositorio
    @Autowired
    private SalesrepRepository salesRepo;

    //así podemos o usar "/salesrep" o "/salesrep/"
    @GetMapping({"", "/"})
    //queremos que devuelva la String del nombre del archivo html apra poder cargar el index.html de la carpeta de salesrep/comerciales
    public String getSalesrep(Model model) {
        //aquí estamos cogiendo todos los clientes del repositorio y los ordenamos mostrando primero los añadidos recientemente, esta lista luego la pasaremos a la página
        //la explicación de por qué estamos utilizando "var" viene detallada en la documentación
        var salesreps = salesRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("salesreplist", salesreps);

        return "salesrep/index";

    }

    @GetMapping("/create")
    public String createSalesrep(Model model) {
        SalesrepDto salesrepDto = new SalesrepDto();
        model.addAttribute("salesrepDto", salesrepDto);

        return "salesrep/create";
    }

    @PostMapping("/create")
    public String createSalesrep(@Valid @ModelAttribute SalesrepDto salesrepDto, BindingResult result) {

        if (salesRepo.findByEmail(salesrepDto.getEmail()) != null) {
            result.addError(
                    new FieldError("salesrepDto", "email", salesrepDto.getEmail(), false, null, null, "Este correo electrónico está ya en uso")
            );
        }

        if (result.hasErrors()) {
            return "salesrep/create";
        }

        Salesrep sales = new Salesrep();
        sales.setFullName(salesrepDto.getFullName());
        sales.setEmail(salesrepDto.getEmail());
        sales.setPhoneNum(salesrepDto.getPhoneNum());
        sales.setStatus(salesrepDto.getStatus());
        sales.setHiringDate(new Date());

        salesRepo.save(sales);

        return "redirect:/salesrep";
    }

    @GetMapping("/edit")
    public String editSalesrep(Model model, @RequestParam int id) {
        Salesrep sales = salesRepo.findById(id).orElse(null);
        if (sales == null) {
            return "redirect:/salesrep";
        }

        SalesrepDto salesrepDto = new SalesrepDto();
        salesrepDto.setFullName(sales.getFullName());
        salesrepDto.setEmail(sales.getEmail());
        salesrepDto.setPhoneNum(sales.getPhoneNum());
        salesrepDto.setStatus(sales.getStatus());

        model.addAttribute("salesrep", sales);
        model.addAttribute("salesrepDto", salesrepDto);

        return "salesrep/edit";
    }

    @PostMapping("/edit")
    public String editSalesrep(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute SalesrepDto salesrepDto,
            BindingResult result
    ) {

        Salesrep sales = salesRepo.findById(id).orElse(null);
        if (sales == null) {
            return "redirect:/salesrep";
        }

        model.addAttribute("salesrep", sales);

        if (result.hasErrors()) {
            return "salesrep/edit";
        }

        sales.setFullName(salesrepDto.getFullName());
        sales.setEmail(salesrepDto.getEmail());
        sales.setPhoneNum(salesrepDto.getPhoneNum());
        sales.setStatus(salesrepDto.getStatus());

        try {
            salesRepo.save(sales);
        } catch (Exception e) {
            result.addError(
                    new FieldError("salesrepDto", "email", salesrepDto.getEmail(), false, null, null, "Correo electrónico ya en uso")
            );

            return "salesrep/edit";
        }

        return "redirect:/salesrep";
    }

    @GetMapping("/delete")
    public String deleteSalesrep(@RequestParam int id) {
        Salesrep sales = salesRepo.findById(id).orElse(null);

        if (sales != null) {
            salesRepo.delete(sales);
        }

        return "redirect:/salesrep";

    }

}
