"use client";

import { CheckCircle, XCircle, ArrowRight, GitFork, ShieldAlert, FileText, Zap } from "lucide-react";

export default function CounterfactualStudio({ params }: { params: { id: string } }) {
  return (
    <div className="flex h-full flex-col p-8 max-w-6xl mx-auto w-full">
      <div className="mb-10 text-center">
        <h1 className="text-4xl font-black text-white tracking-tight flex items-center justify-center mb-2">
          COUNTERFACTUAL STUDIO
          <span className="ml-4 px-2.5 py-1 bg-fuchsia-500/10 text-fuchsia-400 rounded-md text-xs font-bold border border-fuchsia-500/20 uppercase tracking-widest">
            Phase 8 Engine
          </span>
        </h1>
        <p className="text-slate-400 text-lg">Compare real historical execution vs counterfactual replay</p>
      </div>

      <div className="grid grid-cols-2 gap-10">
        
        {/* REAL TIMELINE */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-2xl flex flex-col">
          <div className="bg-slate-800/50 px-6 py-4 border-b border-slate-800 flex justify-between items-center">
            <h2 className="text-lg font-bold text-white flex items-center">
              REAL <span className="ml-2 text-slate-500 font-mono text-sm">MAIN</span>
            </h2>
            <span className="px-3 py-1 bg-rose-500/10 text-rose-400 rounded font-bold text-xs border border-rose-500/20 flex items-center">
              <XCircle size={14} className="mr-1.5" /> FAILED
            </span>
          </div>
          
          <div className="p-6 flex-1 bg-gradient-to-b from-slate-900 to-slate-950">
            <ul className="space-y-5 relative before:absolute before:inset-y-0 before:left-3 before:w-0.5 before:bg-slate-800">
              {['ORDER_CREATED', 'PAYMENT_STARTED', 'PAYMENT_SUCCESS', 'INVENTORY_REQUESTED'].map((evt, i) => (
                <li key={i} className="flex items-center relative z-10 pl-8">
                  <div className="absolute left-1.5 w-3 h-3 bg-slate-700 rounded-full ring-4 ring-slate-900"></div>
                  <span className="font-mono text-sm text-slate-300 bg-slate-800/50 px-3 py-1.5 rounded-lg border border-slate-700 w-full">{evt}</span>
                </li>
              ))}
              <li className="flex items-center relative z-10 pl-8">
                <div className="absolute left-1 w-4 h-4 bg-rose-500 rounded-full ring-4 ring-rose-500/20 flex items-center justify-center">
                  <ShieldAlert size={10} className="text-white" />
                </div>
                <span className="font-mono text-sm font-bold text-rose-400 bg-rose-500/10 px-3 py-1.5 rounded-lg border border-rose-500/20 w-full flex items-center justify-between">
                  INVENTORY_TIMEOUT
                  <span className="text-[10px] text-rose-500/70 uppercase">FAULT-001</span>
                </span>
              </li>
              <li className="flex items-center relative z-10 pl-8 opacity-70">
                <div className="absolute left-1.5 w-3 h-3 bg-rose-900 rounded-full ring-4 ring-slate-900"></div>
                <span className="font-mono text-sm text-rose-300/80 px-3 py-1.5 w-full">ORDER_FAILED</span>
              </li>
            </ul>
          </div>
        </div>

        {/* COUNTERFACTUAL TIMELINE */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-2xl shadow-emerald-500/5 flex flex-col relative">
          <div className="absolute inset-0 bg-emerald-500/5 pointer-events-none"></div>
          <div className="bg-emerald-950/30 px-6 py-4 border-b border-emerald-900/50 flex justify-between items-center z-10">
            <h2 className="text-lg font-bold text-white flex items-center">
              WHAT IF <span className="ml-2 text-emerald-500/70 font-mono text-sm">{params.id.toUpperCase()}</span>
            </h2>
            <span className="px-3 py-1 bg-emerald-500/20 text-emerald-400 rounded font-bold text-xs border border-emerald-500/30 flex items-center">
              <CheckCircle size={14} className="mr-1.5" /> COMPLETED
            </span>
          </div>
          
          <div className="p-6 flex-1 bg-gradient-to-b from-slate-900 to-slate-950 z-10">
            <ul className="space-y-5 relative before:absolute before:inset-y-0 before:left-3 before:w-0.5 before:bg-slate-800">
              {['ORDER_CREATED', 'PAYMENT_STARTED', 'PAYMENT_SUCCESS', 'INVENTORY_REQUESTED'].map((evt, i) => (
                <li key={i} className="flex items-center relative z-10 pl-8 opacity-50">
                  <div className="absolute left-1.5 w-3 h-3 bg-slate-700 rounded-full ring-4 ring-slate-900"></div>
                  <span className="font-mono text-sm text-slate-300 px-3 py-1.5 w-full">{evt}</span>
                </li>
              ))}
              <li className="flex items-center relative z-10 pl-8 mt-8 before:absolute before:-top-4 before:left-8 before:text-[10px] before:text-emerald-500 before:font-bold before:content-['REPLAY_START']">
                <div className="absolute left-1 w-4 h-4 bg-emerald-500 rounded-full ring-4 ring-emerald-500/20 flex items-center justify-center">
                  <GitFork size={10} className="text-white" />
                </div>
                <span className="font-mono text-sm font-bold text-emerald-400 bg-emerald-500/10 px-3 py-1.5 rounded-lg border border-emerald-500/20 w-full flex items-center justify-between shadow-[0_0_15px_rgba(16,185,129,0.1)]">
                  INVENTORY_RESERVED
                  <span className="text-[10px] text-emerald-500/70 uppercase">GENERATED</span>
                </span>
              </li>
              <li className="flex items-center relative z-10 pl-8">
                <div className="absolute left-1.5 w-3 h-3 bg-emerald-700 rounded-full ring-4 ring-slate-900"></div>
                <span className="font-mono text-sm font-bold text-emerald-300 bg-emerald-500/5 px-3 py-1.5 rounded-lg border border-emerald-500/10 w-full flex items-center justify-between">
                  SHIPPING_CREATED
                  <span className="text-[10px] text-emerald-500/70 uppercase">GENERATED</span>
                </span>
              </li>
              <li className="flex items-center relative z-10 pl-8">
                <div className="absolute left-1.5 w-3 h-3 bg-emerald-700 rounded-full ring-4 ring-slate-900"></div>
                <span className="font-mono text-sm font-bold text-emerald-300 bg-emerald-500/5 px-3 py-1.5 rounded-lg border border-emerald-500/10 w-full flex items-center justify-between">
                  ORDER_COMPLETED
                  <span className="text-[10px] text-emerald-500/70 uppercase">GENERATED</span>
                </span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      {/* OUTCOME SUMMARY */}
      <div className="mt-10 bg-slate-900 border border-slate-800 rounded-2xl p-8 flex items-center justify-between shadow-xl">
        <div>
          <h3 className="text-slate-400 font-bold text-sm uppercase tracking-wider mb-1">Outcome Transformation</h3>
          <div className="flex items-center text-3xl font-black mt-2">
            <span className="text-rose-500">FAILED</span>
            <ArrowRight className="mx-6 text-slate-600" size={32} />
            <span className="text-emerald-500">COMPLETED</span>
          </div>
        </div>
        
        <div className="flex space-x-12 border-l border-slate-800 pl-12">
          <div>
            <h4 className="text-slate-500 text-xs font-bold uppercase mb-2 flex items-center"><ShieldAlert size={12} className="mr-1.5 text-rose-500"/> Cause Removed</h4>
            <div className="text-white font-mono text-sm bg-slate-800 px-3 py-1.5 rounded border border-slate-700">FAULT-001 (DROP)</div>
          </div>
          <div>
            <h4 className="text-slate-500 text-xs font-bold uppercase mb-2 flex items-center"><GitFork size={12} className="mr-1.5 text-indigo-400"/> Fork Point</h4>
            <div className="text-white font-mono text-sm">INVENTORY_REQUESTED</div>
          </div>
          <div>
            <h4 className="text-slate-500 text-xs font-bold uppercase mb-2 flex items-center"><Zap size={12} className="mr-1.5 text-amber-400"/> Replay Evidence</h4>
            <div className="flex flex-col">
              <span className="text-emerald-400 font-bold text-sm">DETERMINISTIC</span>
              <span className="text-slate-400 font-mono text-xs mt-0.5">Seed: 42819</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
