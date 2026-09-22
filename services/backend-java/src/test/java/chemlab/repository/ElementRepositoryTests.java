package chemlab.repository;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.domain.repository.ElementRepository;
import chemlab.domain.service.user.UserService;
import chemlab.infrastructure.fastapiworker.service.FastApiWorkerService;
import chemlab.infrastructure.pubchem.PubChemElement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ElementRepositoryTests {

    @Autowired
    private ElementRepository elmRepo;

    @MockitoBean
    FastApiWorkerService fastApiWorkerService;

    @MockitoBean
    UserService userService;

    @Test
    @DisplayName("should find all the elements in the periodic table")
    void testFindById() throws FailedToLoadPTException {
        // Arrange
        List<PubChemElement> elms = elmRepo.findAll();
        assertEquals(118, elms.size());
    }
}
