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
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class CepService {
    private static Logger logger = LoggerFactory.getLogger(CepService.class);
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AddressStatusRepository addressStatusRepository;
    @Autowired
    private SetupRepository setupRepository;

    public Status getStatus() {
        return addressStatusRepository.findById(AddressStatus.DEFAULT_ID)
                .orElse(AddressStatus.builder().status(Status.NEED_SETUP).build()).getStatus();
    }

    public Address getAddressByZipcode(String zipcode) throws NoContentException, NotReadyException {
        if (!getStatus().equals(Status.READY)) throw new NotReadyException();
        return addressRepository.findById(zipcode)
                .orElseThrow(NoContentException::new);
    }

    public void setup() throws Exception {
        logger.info("-----");
        logger.info("-----");
        logger.info("----- SETUP RUNNING");
        logger.info("-----");
        logger.info("-----");

        if (getStatus().equals(Status.NEED_SETUP)) {
            saveStatus(Status.SETUP_RUNNING);
            try {
                addressRepository.saveAll(setupRepository.getFromOrigin());
            } catch (Exception exp) {
                saveStatus(Status.NEED_SETUP);
                throw exp;
            }
            saveStatus(Status.READY);
        }

        logger.info("-----");
        logger.info("-----");
        logger.info("----- SERVICE READY");
        logger.info("-----");
        logger.info("-----");

    }

    private void saveStatus(Status status) {
        addressStatusRepository.save(AddressStatus.builder()
                .id(AddressStatus.DEFAULT_ID)
                .status(status)
                .build());
    }

    @EventListener(ApplicationStartedEvent.class)
    protected void setupOnStartup() {
        try {
            setup();
        } catch (Exception exp) {
            CepApplication.close(9999);
        }
    }

}
