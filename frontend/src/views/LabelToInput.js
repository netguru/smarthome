import React, { useState, useEffect } from 'react';

const LabelToInput = (props) => {

    const [state, setState] = useState({show: false, value: ""})
    useEffect(()=>{
        setState({show: false})
      }, [])

    const editTitleKeyDown = (e) => {
        setState({...state, value: e.target.value})
        if (e.key === 'Enter') {
            if (e.target.value !== "") {
                props.valueChanged(e.target.value)
            }
            setState({ ...state, show: false })
        }
    }

    const showEditTitle = (show) => {
        setState({ ...state, show: show })
    }

    return (
        <div>
            {state.show &&
                <input className="input" type="text" defaultValue={props.name}
                    onKeyDown={editTitleKeyDown}
                    onBlur={() => {
                        props.valueChanged(state.value);
                        setState({...state, show: false})
                    }}></input>
            }
            {!state.show &&
                <div onClick={() => showEditTitle(true)}>
                    {props.name}
                </div>
            }
        </div>
    )
}

export default LabelToInput;