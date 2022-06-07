export interface Palette {
    backgroundColor: string;
    color: string;
}

export class ColorChoice {
    private index = 0;
    private colors: Palette[] = [
        { color: 'white', backgroundColor: 'red' },
        { color: 'white', backgroundColor: 'blue' },
        { color: 'white', backgroundColor: 'darkgreen' },
    ]

    nextColor(): Palette {
        this.index = (this.index + 1) % this.colors.length;
        return this.colors[this.index];
    }
}