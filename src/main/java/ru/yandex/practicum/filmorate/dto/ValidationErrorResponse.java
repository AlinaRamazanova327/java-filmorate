package ru.yandex.practicum.filmorate.dto;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}