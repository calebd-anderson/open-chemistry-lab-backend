package chemlab.domain.model.chemistry;

import chemlab.shared.requests.ReactionRequest;
import chemlab.shared.responses.ReactionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    // For Incoming Requests (DTO $\to$ Entity)
    // FIX ERROR 2: Explicitly ignore system-managed fields
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "formula", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "firstDiscoveredWhen", ignore = true)
    @Mapping(target = "firstDiscoveredBy", ignore = true)
    @Mapping(target = "lastDiscoveredWhen", ignore = true)
    @Mapping(target = "lastDiscoveredBy", ignore = true)
    @Mapping(target = "discoveredCount", ignore = true)
    // FIX ERROR 1: Tell MapStruct to use the existing conversion logic in ReactionRequest
    @Mapping(target = "elements", source = "mappedPayload")
    Reaction toEntity(ReactionRequest request);

    // For Outgoing Responses (Entity $\to$ DTO)
    ReactionResponse toResponse(Reaction entity);

    // For Updates (DTO $\to$ Existing Entity)
    // --- UPDATE LOGIC ---
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "formula", ignore = true)
    // Use the helper method in ReactionRequest to bridge the type mismatch
    @Mapping(target = "elements", source = "mappedPayload")
    // Note on target = "title": I added ignore = true for title in the update method as well.
    // Usually, in a "Discovery" workflow, you don't want a user to be able to change the official title of a chemical compound via a simple update request; that should be handled by your service logic or a separate admin endpoint.
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "firstDiscoveredWhen", ignore = true)
    @Mapping(target = "firstDiscoveredBy", ignore = true)
    @Mapping(target = "lastDiscoveredWhen", ignore = true)
    @Mapping(target = "lastDiscoveredBy", ignore = true)
    @Mapping(target = "discoveredCount", ignore = true)
    void updateEntityFromRequest(ReactionRequest request, @MappingTarget Reaction entity);
}
