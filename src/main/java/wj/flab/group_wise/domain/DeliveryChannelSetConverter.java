package wj.flab.group_wise.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import wj.flab.group_wise.domain.Notification.DeliveryChannel;

@Converter
public class DeliveryChannelSetConverter implements AttributeConverter<Set<DeliveryChannel>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<DeliveryChannel> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
            .map(DeliveryChannel::name)
            .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<DeliveryChannel> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(dbData.split(DELIMITER))
            .map(String::trim)
            .map(DeliveryChannel::valueOf)
            .collect(Collectors.toSet());
    }
}
