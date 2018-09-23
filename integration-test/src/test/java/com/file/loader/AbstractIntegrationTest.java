package com.file.loader;

import com.file.loader.utils.ProfileConstants;
import com.sapsystem.utils.ProfileConstants;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        classes = ApplicationLauncher.class
)
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles(ProfileConstants.TEST_PROFILE)
public abstract class AbstractIntegrationTest {

    protected final MockMvc mockMvc;

    protected AbstractIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

}
