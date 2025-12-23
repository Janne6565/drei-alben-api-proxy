package com.janne.dreialbenapiproxy.model.incomming.diedreimetadaten;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record DreiMetaDatenAlbumDto(int nummer,
                                    String titel,
                                    String autor,

                                    @JsonProperty("hörspielskriptautor")
                                    String hoerspielskriptautor,

                                    String gesamtbeschreibung,
                                    String kurzbeschreibung,
                                    String beschreibung,

                                    @JsonProperty("veröffentlichungsdatum")
                                    LocalDate veroeffentlichungsdatum,

                                    @JsonProperty("gesamtdauer")
                                    long gesamtdauer,

                                    List<Kapitel> kapitel,
                                    List<Sprechrolle> sprechrollen,
                                    Links links,
                                    Ids ids,
                                    List<Medium> medien) {
}
