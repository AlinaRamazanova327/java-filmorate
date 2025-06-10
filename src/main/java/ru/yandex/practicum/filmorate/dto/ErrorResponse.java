package ru.yandex.practicum.filmorate.dto;

import java.util.List;

public record ErrorResponse(List<Violation> violations) {
}