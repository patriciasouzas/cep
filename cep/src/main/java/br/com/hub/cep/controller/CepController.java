package br.com.hub.cep.controller;

import br.com.hub.cep.exception.NoContentException;
import br.com.hub.cep.exception.NotReadyException;
import br.com.hub.cep.model.Address;
import br.com.hub.cep.service.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CepController {

    @Autowired
    private CepService service;

    @GetMapping("/status")
    public String getStatus() {
        return "Service status: " + service.getStatus();
    }

    @GetMapping("/zipcode/{zipcode}")
    public Address getAddressByZipcode(@PathVariable("zipcode") String zipcode) throws NoContentException, NotReadyException {
        return service.getAddressByZipcode(zipcode);
    }
}
