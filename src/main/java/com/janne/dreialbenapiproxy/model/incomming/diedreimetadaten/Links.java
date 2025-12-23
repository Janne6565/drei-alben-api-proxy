package com.janne.dreialbenapiproxy.model.incomming.diedreimetadaten;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Links(
    String json,
    String ffmetadata,
    String cover,

    @JsonProperty("cover_itunes")
    String coverItunes,

    String dreifragezeichen,
    String appleMusic,
    String spotify,
    String amazonMusic,
    String amazon,
    String youTubeMusic,
    String deezer
) {}
