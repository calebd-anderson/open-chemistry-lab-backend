package chemlab;

import chemlab.infrastructure.persistence.user.RegisteredUserPersistenceAdapter;
import chemlab.security.config.CorsProperties;
import chemlab.infrastructure.fastapiworker.service.FastApiWorkerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChemistryApplicationTests {

	@MockitoBean
	private CorsProperties corsProperties;
	@MockitoBean
	FastApiWorkerService fastApiWorkerService;
	@MockitoBean
	RegisteredUserPersistenceAdapter registeredUserPersistenceAdapter;
	
	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldReturnDefault() throws Exception {
		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk());
	}

}
