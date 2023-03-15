package br.com.hub.cep.service;

import br.com.hub.cep.CepApplication;
import br.com.hub.cep.exception.NoContentException;
import br.com.hub.cep.exception.NotReadyException;
import br.com.hub.cep.model.Address;
import br.com.hub.cep.model.AddressStatus;
import br.com.hub.cep.model.Status;
import br.com.hub.cep.repository.AddressRepository;
import br.com.hub.cep.repository.AddressStatusRepository;
import br.com.hub.cep.repository.SetupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CepService {
    private static Logger logger = LoggerFactory.getLogger(CepService.class);
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AddressStatusRepository addressStatusRepository;
    @Autowired
    private SetupRepository setupRepository;
    @Value("${setup.on.startup}")
    private boolean setupOnStartup;

    public Status getStatus() {
        return addressStatusRepository.findById(AddressStatus.DEFAULT_ID)
                .orElse(AddressStatus.builder().status(Status.NEED_SETUP).build()).getStatus();
    }

    public Address getAddressByZipcode(String zipcode) {

        Optional<Address> addressOptional = addressRepository.findById(zipcode);

        if (!getStatus().equals(Status.READY))
            throw new NotReadyException("Service is in preparation. Wait a moment, please.");

        if (addressOptional.isEmpty()) {
            throw new NoContentException("Sorry, this zip code was not found.");
        }
        return addressOptional.get();
    }

    public void setup() {
        logger.info("-----");
        logger.info("-----");
        logger.info("----- SETUP RUNNING");
        logger.info("-----");
        logger.info("-----");

        try {
            if (getStatus().equals(Status.NEED_SETUP)) {
                saveStatus(Status.SETUP_RUNNING);

                addressRepository.saveAll(setupRepository.getFromOrigin());

                saveStatus(Status.READY);
            }

            logger.info("-----");
            logger.info("-----");
            logger.info("----- SERVICE READY TO USE");
            logger.info("-----");
            logger.info("-----");

        } catch (Exception exp) {
            logger.error("Error to download/save address, closing the application.", exp);
            CepApplication.close(9999);
        }

    }

    private void saveStatus(Status status) {
        addressStatusRepository.save(AddressStatus.builder()
                .id(AddressStatus.DEFAULT_ID)
                .status(status)
                .build());
    }

    @Async
    @EventListener(ApplicationStartedEvent.class)
    protected void setupOnStartup() {
        if (setupOnStartup) {
            setup();
        }
    }

}
