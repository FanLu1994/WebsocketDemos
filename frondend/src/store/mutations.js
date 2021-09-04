/**
 * @Description:
 * @author fan
 * @date 2021/4/18 16:18
 */


const mutations = {
    setCount(state, v) {
        state.count = v;
    },
    addCount(state,v) {
        if(v){
            state.count += v
        }else{
            state.count ++
        }
    },
    reduceCount(state,v) {
        if(v){
            state.count -= v
        }else{
            state.count --
        }

    }
}
export default mutations
