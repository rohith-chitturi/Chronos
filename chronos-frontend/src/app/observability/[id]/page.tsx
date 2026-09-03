"use client";

import { Activity, Clock, ShieldAlert, GitFork, ArrowRight, Server } from "lucide-react";

export default function DistributedTraceStudio({ params }: { params: { id: string } }) {
  // Hardcoded UI for visual structure based on the architecture.
  // In a real app, we would fetch /api/observability/traces/{id}
  
  return (
    <div className="flex h-full flex-col p-8 max-w-6xl mx-auto w-full">
      <div className="mb-10 text-center">
        <h1 className="text-4xl font-black text-white tracking-tight flex items-center justify-center mb-2">
          DISTRIBUTED TRACE STUDIO
          <span className="ml-4 px-2.5 py-1 bg-cyan-500/10 text-cyan-400 rounded-md text-xs font-bold border border-cyan-500/20 uppercase tracking-widest">
            Phase 9 Engine
          </span>
        </h1>
        <p className="text-slate-400 text-lg">Reconstructed causal delays and fault-attributed latency</p>
      </div>

      <div className="space-y-12">
        {/* REAL TRACE */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-2xl">
          <div className="bg-slate-800/50 px-6 py-4 border-b border-slate-800 flex justify-between items-center">
            <h2 className="text-lg font-bold text-white flex items-center">
              REAL <span className="ml-2 text-slate-500 font-mono text-sm">MAIN TIMELINE</span>
            </h2>
            <div className="flex items-center space-x-4">
               <span className="text-slate-400 font-mono text-sm">Duration: 4120 ms</span>
               <span className="px-3 py-1 bg-rose-500/10 text-rose-400 rounded font-bold text-xs border border-rose-500/20 flex items-center">
                 ANOMALY DETECTED
               </span>
            </div>
          </div>
          
          <div className="p-6 bg-gradient-to-b from-slate-900 to-slate-950 overflow-x-auto">
            {/* Timeline Axis */}
            <div className="flex border-b border-slate-800 pb-2 mb-4 relative ml-48 text-xs font-mono text-slate-500">
              <div className="absolute left-0">0ms</div>
              <div className="absolute left-1/4">1000ms</div>
              <div className="absolute left-2/4">2000ms</div>
              <div className="absolute left-3/4">3000ms</div>
              <div className="absolute right-0">4000ms</div>
            </div>

            {/* Service Lanes */}
            <div className="space-y-6">
              
              {/* Order Service */}
              <div className="flex items-center relative">
                <div className="w-48 flex items-center text-sm font-bold text-indigo-400">
                  <Server size={14} className="mr-2" /> order-service
                </div>
                <div className="flex-1 relative h-6">
                  {/* Span */}
                  <div className="absolute left-0 w-[5%] h-full bg-indigo-500/20 border border-indigo-500/50 rounded-sm flex items-center px-2">
                     <span className="text-[10px] text-indigo-300 font-mono">ORDER_CREATED</span>
                  </div>
                </div>
              </div>

              {/* Payment Service */}
              <div className="flex items-center relative">
                <div className="w-48 flex items-center text-sm font-bold text-emerald-400">
                  <Server size={14} className="mr-2" /> payment-service
                </div>
                <div className="flex-1 relative h-6">
                  {/* Network Delay */}
                  <div className="absolute left-[5%] w-[2%] h-full border-t border-dashed border-emerald-500/30 mt-3"></div>
                  
                  {/* Span - ANOMALY */}
                  <div className="absolute left-[7%] w-[75%] h-full bg-rose-500/20 border-2 border-rose-500/70 rounded-sm flex items-center justify-between px-2 relative group cursor-pointer shadow-[0_0_15px_rgba(244,63,94,0.15)]">
                     <span className="text-[10px] text-rose-300 font-mono">PAYMENT_STARTED</span>
                     
                     <div className="flex items-center bg-rose-500/20 px-2 py-0.5 rounded text-[10px] text-rose-300 font-bold border border-rose-500/30">
                       <ShieldAlert size={10} className="mr-1" /> FAULT: LATENCY
                     </div>
                     
                     <span className="text-[10px] text-rose-300 font-mono">PAYMENT_SUCCESS</span>
                     
                     {/* Tooltip */}
                     <div className="absolute bottom-full mb-2 left-1/2 -translate-x-1/2 w-48 bg-slate-800 text-white p-3 rounded-lg border border-slate-700 opacity-0 group-hover:opacity-100 transition-opacity z-50 shadow-2xl">
                        <div className="text-xs font-bold text-rose-400 mb-1">Fault-Attributed Latency</div>
                        <div className="text-xs text-slate-300 flex justify-between"><span>Normal:</span> <span className="font-mono">500 ms</span></div>
                        <div className="text-xs text-slate-300 flex justify-between"><span>Actual:</span> <span className="font-mono text-rose-400">3500 ms</span></div>
                        <div className="text-xs text-slate-300 flex justify-between border-t border-slate-700 mt-1 pt-1 font-bold"><span>Delta:</span> <span className="text-rose-400">+3000 ms</span></div>
                     </div>
                  </div>
                </div>
              </div>

              {/* Inventory Service */}
              <div className="flex items-center relative">
                <div className="w-48 flex items-center text-sm font-bold text-cyan-400">
                  <Server size={14} className="mr-2" /> inventory-service
                </div>
                <div className="flex-1 relative h-6">
                  {/* Network Delay */}
                  <div className="absolute left-[82%] w-[3%] h-full border-t border-dashed border-cyan-500/30 mt-3"></div>
                  
                  {/* Span */}
                  <div className="absolute left-[85%] w-[10%] h-full bg-cyan-500/20 border border-cyan-500/50 rounded-sm flex items-center justify-between px-2">
                     <span className="text-[10px] text-cyan-300 font-mono truncate">INV_REQUESTED</span>
                     <span className="text-[10px] text-cyan-300 font-mono truncate ml-2">TIMEOUT</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* COUNTERFACTUAL TRACE */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-2xl shadow-emerald-500/5">
          <div className="bg-emerald-950/30 px-6 py-4 border-b border-emerald-900/50 flex justify-between items-center">
            <h2 className="text-lg font-bold text-white flex items-center">
              WHAT IF <span className="ml-2 text-emerald-500/70 font-mono text-sm">EXP-002</span>
            </h2>
            <div className="flex items-center space-x-4">
               <span className="text-emerald-400 font-mono text-sm">Duration: 1120 ms</span>
               <span className="px-3 py-1 bg-emerald-500/20 text-emerald-400 rounded font-bold text-xs border border-emerald-500/30 flex items-center">
                 FAULT REMOVED
               </span>
            </div>
          </div>
          
          <div className="p-6 bg-gradient-to-b from-slate-900 to-slate-950 overflow-x-auto relative">
             <div className="absolute inset-0 bg-emerald-500/5 pointer-events-none"></div>
             
            {/* Timeline Axis */}
            <div className="flex border-b border-slate-800 pb-2 mb-4 relative ml-48 text-xs font-mono text-slate-500 z-10">
              <div className="absolute left-0">0ms</div>
              <div className="absolute left-1/4">1000ms</div>
              <div className="absolute left-2/4">2000ms</div>
              <div className="absolute left-3/4">3000ms</div>
              <div className="absolute right-0">4000ms</div>
            </div>

            {/* Service Lanes */}
            <div className="space-y-6 z-10 relative">
              
              {/* Order Service */}
              <div className="flex items-center relative opacity-50">
                <div className="w-48 flex items-center text-sm font-bold text-indigo-400">
                  <Server size={14} className="mr-2" /> order-service
                </div>
                <div className="flex-1 relative h-6">
                  <div className="absolute left-0 w-[5%] h-full bg-indigo-500/20 border border-indigo-500/50 rounded-sm flex items-center px-2">
                     <span className="text-[10px] text-indigo-300 font-mono">ORDER_CREATED</span>
                  </div>
                </div>
              </div>

              {/* Payment Service */}
              <div className="flex items-center relative">
                <div className="w-48 flex items-center text-sm font-bold text-emerald-400">
                  <Server size={14} className="mr-2" /> payment-service
                </div>
                <div className="flex-1 relative h-6">
                  <div className="absolute left-[5%] w-[2%] h-full border-t border-dashed border-emerald-500/30 mt-3 opacity-50"></div>
                  
                  {/* Span - NORMAL */}
                  <div className="absolute left-[7%] w-[12%] h-full bg-emerald-500/20 border border-emerald-500/50 rounded-sm flex items-center justify-between px-2 relative group cursor-pointer">
                     <span className="text-[10px] text-emerald-300 font-mono truncate">PAYMENT_STARTED</span>
                     <span className="text-[10px] text-emerald-300 font-mono truncate ml-2">SUCCESS</span>
                     
                     {/* Tooltip */}
                     <div className="absolute bottom-full mb-2 left-1/2 -translate-x-1/2 w-40 bg-slate-800 text-white p-3 rounded-lg border border-slate-700 opacity-0 group-hover:opacity-100 transition-opacity z-50 shadow-2xl">
                        <div className="text-xs font-bold text-emerald-400 mb-1">Causal Delay</div>
                        <div className="text-xs text-slate-300 font-mono">500 ms</div>
                     </div>
                  </div>
                </div>
              </div>

              {/* Inventory Service */}
              <div className="flex items-center relative">
                <div className="w-48 flex items-center text-sm font-bold text-cyan-400">
                  <Server size={14} className="mr-2" /> inventory-service
                </div>
                <div className="flex-1 relative h-6">
                  <div className="absolute left-[19%] w-[3%] h-full border-t border-dashed border-cyan-500/30 mt-3"></div>
                  
                  {/* Span */}
                  <div className="absolute left-[22%] w-[5%] h-full bg-cyan-500/20 border border-cyan-500/50 rounded-sm flex items-center justify-between px-2">
                     <span className="text-[10px] text-cyan-300 font-mono truncate">INV_REQUESTED</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
