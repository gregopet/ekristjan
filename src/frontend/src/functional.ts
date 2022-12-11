/** Negates the output of the function given as input */
export function not<X>(fun: (input: X) => boolean): (input: X) => boolean  {
    return (arg: X) => !fun(arg);
}
