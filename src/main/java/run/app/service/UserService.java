package run.app.service;


import lombok.NonNull;
import run.app.entity.DTO.UserDetail;
import run.app.entity.model.BloggerAccount;
import run.app.entity.model.BloggerProfile;
import run.app.entity.model.BloggerProfileWithBLOBs;
import run.app.entity.params.LoginParams;
import run.app.entity.params.UserParams;
import run.app.security.token.AuthToken;

import java.util.List;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: WHOAMI
 * Time: 2019 2019/6/26 21:33
 * Description: ://TODO ${END}
 */

public interface UserService {





     @NonNull
     BloggerProfileWithBLOBs findUserDetailByBloggerId(@NonNull Integer bloggerId);


     @NonNull UserDetail updateProfileById(@NonNull UserParams userParams,@NonNull String token);

     UserDetail getUserDetailByToken(@NonNull String token);




//     Integer getUserIdByToken(@NonNull String token);

     boolean logout(String token);

}
