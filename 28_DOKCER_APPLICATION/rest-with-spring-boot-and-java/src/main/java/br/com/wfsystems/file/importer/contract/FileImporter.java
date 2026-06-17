package br.com.wfsystems.file.importer.contract;

import java.io.InputStream;
import java.util.List;

import br.com.wfsystems.data.dto.v1.PersonDTO;

public interface FileImporter {
    
    List<PersonDTO> importFile(InputStream inputStream) throws Exception;

}
