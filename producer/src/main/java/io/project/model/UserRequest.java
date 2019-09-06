package io.project.model;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Armen Arzumanyan
 */
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private HttpStatus status;
    private String value;

    public HttpStatus getStatus() {
        return status;
    }
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
