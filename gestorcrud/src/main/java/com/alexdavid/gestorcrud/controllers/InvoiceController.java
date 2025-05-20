package com.alexdavid.gestorcrud.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource; // Asegúrate que esta importación esté
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alexdavid.gestorcrud.models.Customer;
import com.alexdavid.gestorcrud.models.Invoice;
import com.alexdavid.gestorcrud.models.InvoiceDto;
import com.alexdavid.gestorcrud.repositories.CustomerRepository;
import com.alexdavid.gestorcrud.repositories.InvoiceRepository;
import com.alexdavid.gestorcrud.services.PdfGenerationService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private PdfGenerationService pdfGenerationService; // Inyectar el servicio

    @GetMapping({"", "/"})
    public String getInvoices(Model model) {
        List<Invoice> invoices = invoiceRepo.findAllByOrderByIdDesc();
        model.addAttribute("invoicelist", invoices);
        return "invoice/index";
    }

    @GetMapping("/create")
    public String createInvoicePage(Model model) {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setIssueDate(new Date()); // Pre-rellenar fecha actual
        model.addAttribute("invoiceDto", invoiceDto);
        List<Customer> customers = customerRepo.findAll(); 
        model.addAttribute("customerlist", customers);
        return "invoice/create";
    }

    @PostMapping("/create")
    public String createInvoice(@Valid @ModelAttribute InvoiceDto invoiceDto, BindingResult result, Model model) {
        Customer customer = customerRepo.findById(invoiceDto.getCustomerId()).orElse(null);
        if (customer == null) {
            result.addError(new FieldError("invoiceDto", "customerId", "Cliente no encontrado."));
        }

        if (result.hasErrors()) {
            List<Customer> customers = customerRepo.findAll();
            model.addAttribute("customerlist", customers);
            return "invoice/create";
        }

        Invoice invoice = new Invoice();
        invoice.setIssueDate(invoiceDto.getIssueDate());
        invoice.setTotalAmount(invoiceDto.getTotalAmount());
        invoice.setStatus(invoiceDto.getStatus());
        invoice.setCustomer(customer);

        invoiceRepo.save(invoice);
        return "redirect:/invoice";
    }

    @GetMapping("/edit")
    public String editInvoicePage(Model model, @RequestParam int id) {
        Invoice invoice = invoiceRepo.findById(id).orElse(null);
        if (invoice == null) {
            return "redirect:/invoice";
        }

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setIssueDate(invoice.getIssueDate());
        invoiceDto.setTotalAmount(invoice.getTotalAmount());
        invoiceDto.setStatus(invoice.getStatus());
        if (invoice.getCustomer() != null) { 
            invoiceDto.setCustomerId(invoice.getCustomer().getId());
        }


        model.addAttribute("invoice", invoice); 
        model.addAttribute("invoiceDto", invoiceDto);
        List<Customer> customers = customerRepo.findAll();
        model.addAttribute("customerlist", customers);

        return "invoice/edit";
    }

    @PostMapping("/edit")
    public String editInvoice(Model model, @RequestParam int id,
                            @Valid @ModelAttribute InvoiceDto invoiceDto, BindingResult result) {

        Invoice invoice = invoiceRepo.findById(id).orElse(null);
        if (invoice == null) {
            return "redirect:/invoice";
        }
         model.addAttribute("invoice", invoice);


        Customer customer = customerRepo.findById(invoiceDto.getCustomerId()).orElse(null);
        if (customer == null) {
            result.addError(new FieldError("invoiceDto", "customerId", "Cliente no encontrado."));
        }
        
        if (result.hasErrors()) {
            List<Customer> customers = customerRepo.findAll();
            model.addAttribute("customerlist", customers);
            return "invoice/edit";
        }

        invoice.setIssueDate(invoiceDto.getIssueDate());
        invoice.setTotalAmount(invoiceDto.getTotalAmount());
        invoice.setStatus(invoiceDto.getStatus());
        invoice.setCustomer(customer);

        invoiceRepo.save(invoice);
        return "redirect:/invoice";
    }

    @GetMapping("/delete")
    public String deleteInvoice(@RequestParam int id) {
        Invoice invoice = invoiceRepo.findById(id).orElse(null);
        if (invoice != null) {
            invoiceRepo.delete(invoice);
        }
        return "redirect:/invoice";
    }

    @GetMapping("/download/{invoiceId}")
    public ResponseEntity<InputStreamResource> downloadInvoicePdf(@PathVariable Integer invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId).orElse(null);

        if (invoice == null) {
            System.err.println("Intento de descargar factura no encontrada con ID: " + invoiceId);
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayInputStream bis = pdfGenerationService.generateInvoicePdf(invoice);

            HttpHeaders headers = new HttpHeaders();
            String filename = "factura_N" + invoice.getId() + ".pdf";
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename); 

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));

        } catch (IOException e) {
            System.err.println("Error al generar PDF para factura ID " + invoiceId + ": " + e.getMessage());
            e.printStackTrace(); 
            return ResponseEntity.status(500).body(null); 
        }
    }
}
