import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import Sidebar from "@/components/Sidebar";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Chronos | Temporal Engine",
  description: "Temporal Distributed Systems Debugger & What-If Simulator",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark">
      <body className={`${inter.className} bg-slate-950 text-slate-100 flex h-screen overflow-hidden`}>
        <Sidebar />
        <main className="flex-1 overflow-y-auto bg-slate-900/50 rounded-tl-3xl border-t border-l border-slate-800 p-8 shadow-2xl">
          {children}
        </main>
      </body>
    </html>
  );
}
