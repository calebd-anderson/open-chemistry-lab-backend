package chemlab.domain.model.game;

import chemlab.shared.requests.CreateFlashcardRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FlashcardMapper {
    Flashcard toEntity(CreateFlashcardRequest request);
    Flashcard toResponse(Flashcard entity);
}
