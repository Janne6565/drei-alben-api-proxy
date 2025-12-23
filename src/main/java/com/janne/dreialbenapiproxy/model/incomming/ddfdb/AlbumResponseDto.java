package com.janne.dreialbenapiproxy.model.incomming.ddfdb;

import java.util.List;

public record AlbumResponseDto(List<AlbumDtoIncomming> items, int total, int offset, int limit) {
}
