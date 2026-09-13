package chemlab.repository;

import chemlab.domain.repository.ElementRepository;
import chemlab.infrastructure.fastapiworker.service.FastApiWorkerService;
import chemlab.infrastructure.persistence.user.UserPersistenceAdapter;
import chemlab.infrastructure.persistence.user.UserReactionPersistenceAdapter;
import chemlab.security.config.CorsProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ElementRepositoryTests {

    @MockitoBean
    private CorsProperties corsProperties;
    @MockitoBean
    FastApiWorkerService fastApiWorkerService;
    @MockitoBean
    UserPersistenceAdapter userPersistenceAdapter;
    @MockitoBean
    UserReactionPersistenceAdapter userReactionPersistenceAdapter;
    @Autowired
    ElementRepository elementRepository;

    @Test
    @DisplayName("should instantiate the repo")
    void testInit() {
        assertNotNull(elementRepository);
    }

//	@Test
//	@DisplayName("should find element with symbol H in the repo")
//	void testFindBySymbol() {
//		Element elm = elementRepo.findElementBySymbol("H");
//		assertEquals(elm.getSymbol(), "H");
//	}

}
