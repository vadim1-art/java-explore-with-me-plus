package ru.practicum.event;

import lombok.experimental.UtilityClass;
import ru.practicum.category.CategoryMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.UserMapper;

@UtilityClass
public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        if (event == null) {
            return null;
        }

        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());

        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));

        return dto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        if (event == null) {
            return null;
        }

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .confirmedRequests(0)
                .views(0L)
                .build();
    }
}
