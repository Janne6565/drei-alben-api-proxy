package com.janne.dreialbenapiproxy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

import java.util.List;

public record AlbumDto(
    String _id,
    List<ImageDto> images,
    String name,
    @Nullable String number,
    AlbumType type,
    float rating,
    @JsonProperty("number_of_ratings") float numberOfRatings,
    float popularity,
    @JsonProperty("release_date") String releaseDate,
    @JsonProperty("updated_at") String updatedAt,
    @JsonProperty("spotify_id") String spotifyId,
    @JsonProperty("deezer_id") String deezerId,
    @JsonProperty("inhalt") String description,
    @JsonProperty("isHidden") Boolean isHidden,
    @JsonProperty("sprecher") String narrator,
    String weblink
) {
}

