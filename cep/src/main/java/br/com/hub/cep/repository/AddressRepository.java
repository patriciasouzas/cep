package br.com.hub.cep.repository;

import br.com.hub.cep.model.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, String> {
}
