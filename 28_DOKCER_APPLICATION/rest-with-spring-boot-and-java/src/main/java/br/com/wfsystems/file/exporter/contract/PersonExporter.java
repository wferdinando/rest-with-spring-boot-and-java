package br.com.wfsystems.file.exporter.contract;
import java.util.List;

import org.springframework.core.io.Resource;

import br.com.wfsystems.data.dto.v1.PersonDTO;

public interface PersonExporter {
    
    Resource exportPeople(List<PersonDTO> people) throws Exception;
    Resource exportPerson(PersonDTO person) throws Exception;
    


}
