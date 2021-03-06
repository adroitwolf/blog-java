package run.app.entity.enums.converter;

import org.springframework.stereotype.Component;
import run.app.entity.enums.RoleEnum;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/10/30 21:23
 * Description: 角色转换器
 */
@Component
public class RoleConverter extends BaseConverter<RoleEnum, String> {
    public RoleConverter() {
        super(RoleEnum.class);
    }
}
