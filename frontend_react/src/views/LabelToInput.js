import React, { useState, useEffect } from 'react';

const LabelToInput = (props) => {

    const [state, setState] = useState({show: false})
    useEffect(()=>{
        setState({show: false})
      }, [])

    const editTitleKeyDown = (e) => {
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
                    onKeyDown={editTitleKeyDown}></input>
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