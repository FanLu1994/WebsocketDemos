/**
 * @Description:
 * @author fan
 * @date 2021/4/18 16:18
 */


const actions = {
    ADD_COUNT(vuex,num) {
        // axios.get("/customer/user_info").then(res => {
        //     commit(TYPES.SET_COUMT, res.data);
        // });
        vuex.commit("addCount", num);
    },
    REDUCE_COUNT(vuex,num) {
        // axios.get("/customer/user_info").then(res => {
        //     commit(TYPES.SET_COUMT, res.data);
        // });
        vuex.commit("reduceCount", num);
    }
}
export default actions
