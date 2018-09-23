package com.file.loader.stepdefs;

import com.sapsystem.AbstractIntegrationTest;
import com.sapsystem.domain.AssetEntity;
import com.sapsystem.repository.AssetRepository;
import com.sapsystem.utils.DBConfig;
import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AssetsStepDefs extends AbstractIntegrationTest {

    private final AssetRepository assetRepository;
    private final DBConfig dbConfig;

    public AssetsStepDefs(MockMvc mockMvc, AssetRepository assetRepository, DBConfig dbConfig) {
        super(mockMvc);
        this.assetRepository = assetRepository;
        this.dbConfig = dbConfig;
    }

    @Given("^populate the following assets data$")
    public void populateSensorData(DataTable dataTable) {
        List<Map<String, String>> maps = dataTable.asMaps(String.class, String.class);
        maps.stream()
                .map(this::convert)
                .forEach(assetRepository::save);
    }

    @Before
    public void setup() {
        dbConfig.deleteAndCreateSequence();
        assetRepository.deleteAll();
    }

    private AssetEntity convert(Map<String, String> map) {
        AssetEntity assetEntity = new AssetEntity();

        assetEntity.setId(Long.parseLong(map.get("id")));
        assetEntity.setTagName(map.get("tagName"));
        assetEntity.setLastMaintenanceDate(Instant.ofEpochMilli(Long.parseLong(map.get("lastMaintenanceDate"))));
        assetEntity.setManufacturer(map.get("manufacturer"));
        assetEntity.setProductNumber(map.get("productNumber"));
        assetEntity.setSerialNumber(Integer.valueOf(map.get("serialNumber")));
        assetEntity.setType(map.get("type"));
        assetEntity.setLongTag(map.get("longTag"));
        return assetEntity;
    }
}

