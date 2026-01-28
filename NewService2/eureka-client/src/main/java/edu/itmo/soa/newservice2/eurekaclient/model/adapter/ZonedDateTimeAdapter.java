package edu.itmo.soa.newservice2.eurekaclient.model.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    @Override
    public ZonedDateTime unmarshal(String v) {
        return v == null ? null : ZonedDateTime.parse(v, FORMATTER);
    }
    @Override
    public String marshal(ZonedDateTime v) {
        return v == null ? null : v.format(FORMATTER);
    }
}
