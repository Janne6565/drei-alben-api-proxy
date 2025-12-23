package com.janne.dreialbenapiproxy.model.incomming.ddfdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.janne.dreialbenapiproxy.model.AlbumDto;
import com.janne.dreialbenapiproxy.model.AlbumType;
import com.janne.dreialbenapiproxy.model.ImageDto;
import jakarta.annotation.Nullable;

import java.util.List;

public record AlbumDtoIncomming(
    String _id,
    List<ImageDto> images,
    String name,
    @Nullable String number,
    AlbumType type,
    Float rating,
    @JsonProperty("number_of_ratings") Float numberOfRatings,
    Float popularity,
    @JsonProperty("release_date") String releaseDate,
    @JsonProperty("updated_at") String updatedAt,
    @JsonProperty("spotify_id") String spotifyId,
    @JsonProperty("deezer_id") String deezerId,
    @JsonProperty("inhalt") String description,
    @JsonProperty("isHidden") Boolean isHidden,
    @JsonProperty("sprecher") String narrator,
    String weblink
) {
    public AlbumDto toAlbumDto() {
        return AlbumDto.builder()
            ._id(_id)
            .images(images)
            .name(name)
            .number(number)
            .type(type)
            .rating(rating)
            .numberOfRatings(numberOfRatings)
            .popularity(popularity)
            .releaseDate(releaseDate)
            .updatedAt(updatedAt)
            .spotifyId(spotifyId)
            .deezerId(deezerId)
            .description(description)
            .isHidden(isHidden)
            .narrator(narrator)
            .weblink(weblink)
            .build();
    }
}

