package com.janne.dreialbenapiproxy.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record AlbumDto(
    String _id,
    List<ImageDto> images,
    String name,
    @Nullable String number,
    AlbumType type,
    Float rating,
    Float numberOfRatings,
    Float popularity,
    String releaseDate,
    String updatedAt,
    String spotifyId,
    String appleMusicId,
    String deezerId,
    String description,
    Boolean isHidden,
    String narrator,
    String weblink
) {

    public AlbumDto.AlbumDtoBuilder toBuilder() {
        return AlbumDto.builder()
            ._id(this._id)
            .images(this.images)
            .name(this.name)
            .number(this.number)
            .type(this.type)
            .rating(this.rating)
            .numberOfRatings(this.numberOfRatings)
            .popularity(this.popularity)
            .releaseDate(this.releaseDate)
            .updatedAt(this.updatedAt)
            .spotifyId(this.spotifyId)
            .appleMusicId(this.appleMusicId)
            .deezerId(this.deezerId)
            .description(this.description)
            .isHidden(this.isHidden)
            .narrator(this.narrator)
            .weblink(this.weblink);
    }
}

