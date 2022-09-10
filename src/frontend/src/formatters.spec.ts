import { expect, test } from 'vitest'
import { stripSeconds, date2Time } from "@/formatters";

test("StripSeconds", () => {
    expect(stripSeconds("02:01:12.700185132")).toBe("2:01")
    expect(stripSeconds("13:01:12.700185132")).toBe("13:01")
    expect(stripSeconds(null)).toBe('')
})

test("date2Time", () => {
    expect(date2Time("2022-09-10T07:41:00.946733+02:00")).toBe("7:41")
    expect(date2Time("2022-09-10T17:41:00.946733+02:00")).toBe("17:41")
    expect(date2Time(null)).toBe(null)
});