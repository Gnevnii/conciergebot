package ru.gnev.conciergebot.persist.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gnev.conciergebot.bean.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    User getUserByTgUserId(long tgUserId);

    boolean existsByTgUserId(long tgUserId);

    @Query(nativeQuery = true, value = "select s_tg_user_name from concierge_db.cb_user where i_floor = (select i_floor from concierge_db.cb_user where l_tg_user_id = ?1)")
    List<String> getSameFloorUsers(long tgUserId);

    @Query(nativeQuery = true, value = "select s_tg_user_name from concierge_db.cb_user where i_section = (select i_section from concierge_db.cb_user where l_tg_user_id = ?1)")
    List<String> getSameSectionUsers(long tgUserId);

    @Query(nativeQuery = true, value = """
            with me as (select i_floor, i_section from concierge_db.cb_user where l_tg_user_id = ?1) 
                        select s_tg_user_name from concierge_db.cb_user usr, me where usr.i_section = me.i_section and usr.i_floor = (me.i_floor + 1)""")
    List<String> getUpperUserUsers(long tgUserId);

    @Query(nativeQuery = true, value = """
            with me as (select i_floor, i_section from concierge_db.cb_user where l_tg_user_id = ?1) 
                        select s_tg_user_name from concierge_db.cb_user usr, me where usr.i_section = me.i_section and usr.i_floor = (me.i_floor - 1)""")
    List<String> getBottomUserUsers(long tgUserId);

    List<User> getUsersByFloorNumber(int floor);

    List<User> getUsersByFlatNumber(int flat);

    List<User> getUsersBySectionNumber(int section);

    List<User> getUsersByFloorNumberAndSectionNumber(int floor, int section);

    @Query(nativeQuery = true, value = """
        UPDATE concierge_db.cb_user SET b_deleted = true, i_floor = 0, i_section = 0, i_flat = 0 WHERE l_tg_user_id = ?1""")
    void markDeleted(long tgUserId);
}
